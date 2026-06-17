package com.farmterest.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dao.AiUsageDAO;
import com.farmterest.model.dto.AiResult;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.service.OpenRouterClient.ChatResult;
import com.farmterest.util.AppConfig;
import com.farmterest.util.SearchCriteria;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * AI 검색 오케스트레이터.
 * 규칙기반(QueryParser)을 기본값으로 깔고, 가능하면 LLM으로 고도화한다.
 * 안전장치: 월 예산 초과/키 없음/오류 시 자동으로 규칙기반으로 폴백.
 * "실시간 동기화": 매 검색마다 라이브 상품 카탈로그를 LLM에 전달.
 */
public class AiSearchService {

    private final OpenRouterClient client = new OpenRouterClient();
    private final AiUsageDAO usageDAO = new AiUsageDAO();
    private final QueryParser parser = new QueryParser();

    private static final int MAX_CATALOG = 60;     // 프롬프트 토큰 제한
    private static final int MAX_TOKENS = 600;

    public AiResult enhance(String query, List<ProductDTO> catalog) {
        AiResult r = new AiResult();
        // 1) 항상 규칙기반을 기본값으로 (LLM이 안 되면 이게 그대로 쓰임)
        r.setCriteria(parser.parse(query));
        r.setUsedLlm(false);

        if (!AppConfig.llmEnabled()) {
            r.setStatus("disabled");
            return r;
        }

        String ym = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        try {
            // 2) 예산 차단: 월 누적이 소프트 한도 이상이면 LLM 사용 안 함
            double spent = usageDAO.monthCost(ym);
            if (spent >= AppConfig.softCapUsd()) {
                r.setStatus("budget");
                return r;
            }

            // 3) 저렴한 모델 자동 선정
            String model = AppConfig.autoModel() ? client.selectCheapestModel() : AppConfig.model();

            // 4) 호출
            ChatResult cr = client.chat(model, systemPrompt(), userPrompt(query, catalog), MAX_TOKENS);

            // 5) 비용 누적(실제 cost). cost 미제공 시 토큰 기반 보수적 추정.
            double cost = cr.cost > 0 ? cr.cost
                    : (cr.promptTokens + cr.completionTokens) * 0.0000006;
            usageDAO.addUsage(ym, cost);

            // 6) 응답 파싱 → 필터/설명/추천 id
            JsonObject obj = extractJson(cr.content);
            applyToCriteria(obj, r.getCriteria());
            r.setExplanation(optString(obj, "explanation"));
            r.setProductIds(optIntList(obj, "productIds"));
            r.setModel(model);
            r.setUsedLlm(true);
            r.setStatus("ai");
        } catch (Exception e) {
            // 어떤 실패든 규칙기반으로 안전하게 폴백
            System.err.println("[AiSearchService] LLM 실패, 규칙기반 폴백: " + e.getMessage());
            r.setStatus("error");
        }
        return r;
    }

    /** 이번 달 누적 비용(USD) — 화면 표시용. */
    public double monthSpent() {
        try {
            return usageDAO.monthCost(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------- 프롬프트 ----------
    private String systemPrompt() {
        return "You are the AI shopping assistant for '팜터레스트', a Korean Gangwon-do (강원도) farm & seafood "
                + "direct-trade store. Given the user's Korean query and the LIVE product catalog (JSON), pick the "
                + "best matching products and infer search filters.\n"
                + "Categories are EXACTLY one of: 쌀, 잡곡, 감자, 채소, 수산. "
                + "sort is one of: relevance, price_asc, price_desc, quality, newest.\n"
                + "Respond with ONLY a JSON object (no markdown, no commentary):\n"
                + "{\"category\":string|null,\"region\":string|null,\"minPolishedRate\":int|null,"
                + "\"sort\":string,\"productIds\":[int],\"explanation\":string}\n"
                + "Rules: productIds must exist in the catalog (max 6, best first). "
                + "explanation = 1~2 friendly KOREAN sentences that mention real product names and quality indicators "
                + "(정백도/완전립/식미치 등). Use ONLY catalog data; never invent products.";
    }

    private String userPrompt(String query, List<ProductDTO> catalog) {
        JsonArray arr = new JsonArray();
        int n = 0;
        for (ProductDTO p : catalog) {
            if (n++ >= MAX_CATALOG) break;
            JsonObject o = new JsonObject();
            o.addProperty("id", p.getProductId());
            o.addProperty("name", p.getName());
            o.addProperty("category", p.getCategory());
            o.addProperty("region", p.getRegion());
            o.addProperty("price", p.getPrice());
            o.addProperty("stock", p.getStock());
            if (p.getPolishedRate() != null) o.addProperty("polishedRate", p.getPolishedRate());
            if (p.getTasteScore() != null) o.addProperty("tasteScore", p.getTasteScore());
            arr.add(o);
        }
        return "질의(Query): " + (query == null ? "" : query) + "\n\n[LIVE CATALOG]\n" + arr.toString();
    }

    // ---------- 파싱 ----------
    private JsonObject extractJson(String content) {
        String s = content == null ? "" : content.trim();
        int i = s.indexOf('{');
        int j = s.lastIndexOf('}');
        if (i >= 0 && j > i) {
            s = s.substring(i, j + 1);
        }
        return JsonParser.parseString(s).getAsJsonObject();
    }

    private void applyToCriteria(JsonObject obj, SearchCriteria c) {
        String cat = optString(obj, "category");
        if (isCategory(cat)) c.setCategory(cat);
        String region = optString(obj, "region");
        if (region != null && !region.isBlank() && !"null".equalsIgnoreCase(region)) c.setRegion(region);
        Integer mp = optInt(obj, "minPolishedRate");
        if (mp != null) c.setMinPolishedRate(mp);
        String sort = optString(obj, "sort");
        if (isSort(sort)) c.setSort(sort);
        // 사람이 읽는 요약은 LLM 설명으로 대체 표시되므로 understood는 유지
    }

    private boolean isCategory(String s) {
        return s != null && (s.equals("쌀") || s.equals("잡곡") || s.equals("감자") || s.equals("채소") || s.equals("수산"));
    }

    private boolean isSort(String s) {
        return s != null && (s.equals("relevance") || s.equals("price_asc") || s.equals("price_desc")
                || s.equals("quality") || s.equals("newest"));
    }

    private String optString(JsonObject o, String k) {
        try {
            if (o.has(k) && !o.get(k).isJsonNull()) {
                String v = o.get(k).getAsString();
                return v == null ? null : v.trim();
            }
        } catch (Exception ignore) {}
        return null;
    }

    private Integer optInt(JsonObject o, String k) {
        try {
            if (o.has(k) && !o.get(k).isJsonNull()) return o.get(k).getAsInt();
        } catch (Exception ignore) {}
        return null;
    }

    private List<Integer> optIntList(JsonObject o, String k) {
        List<Integer> list = new ArrayList<>();
        try {
            if (o.has(k) && o.get(k).isJsonArray()) {
                for (JsonElement e : o.getAsJsonArray(k)) {
                    try { list.add(e.getAsInt()); } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return list;
    }
}

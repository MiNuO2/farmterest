package com.farmterest.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.farmterest.util.AppConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * OpenRouter(LLM) 호출 클라이언트.
 * - selectCheapestModel(): 모델 목록에서 가장 저렴한 모델 자동 선정(캐시)
 * - chat(): 채팅 완성 호출 + 실제 비용(usage.cost) 반환
 * 외부 라이브러리는 Gson(JSON)만 사용, HTTP는 JDK 표준 HttpClient.
 */
public class OpenRouterClient {

    private static final String BASE = "https://openrouter.ai/api/v1";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15)).build();

    /** 자동선정 1순위 후보(저렴+신뢰도 높은 채팅 모델). 이 중 가장 싼 것을 고른다. */
    private static final String[] PREFERRED = {
            "google/gemini-2.0-flash-001",
            "google/gemini-2.5-flash-lite",
            "google/gemini-flash-1.5-8b",
            "openai/gpt-4o-mini",
            "mistralai/ministral-8b",
            "meta-llama/llama-3.1-8b-instruct",
            "deepseek/deepseek-chat"
    };
    private static final String SAFE_DEFAULT = "google/gemini-2.0-flash-001";

    private static volatile String cachedModel = null;
    private static volatile long cachedAt = 0;
    private static final long MODEL_TTL_MS = 6 * 3600 * 1000L;

    public static class ChatResult {
        public String content;
        public double cost;          // USD (OpenRouter usage.cost)
        public int promptTokens;
        public int completionTokens;
        public String model;
    }

    /** 가장 저렴한 모델 자동 선정 (6시간 캐시). 실패 시 안전 기본값. */
    public String selectCheapestModel() {
        long now = System.currentTimeMillis();
        if (cachedModel != null && now - cachedAt < MODEL_TTL_MS) {
            return cachedModel;
        }
        String chosen = SAFE_DEFAULT;
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/models"))
                    .header("Authorization", "Bearer " + AppConfig.apiKey())
                    .timeout(Duration.ofSeconds(15)).GET().build();
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() == 200) {
                JsonArray data = JsonParser.parseString(resp.body()).getAsJsonObject().getAsJsonArray("data");
                Map<String, Double> prices = new HashMap<>();
                double globalMin = Double.MAX_VALUE;
                String globalMinId = null;
                for (JsonElement el : data) {
                    JsonObject mo = el.getAsJsonObject();
                    if (!isTextModel(mo)) {
                        continue;
                    }
                    String id = mo.get("id").getAsString();
                    double price = price(mo);
                    if (price <= 0) {
                        continue; // 무료/미정 모델 제외(요청 한도 불안정)
                    }
                    prices.put(id, price);
                    if (price < globalMin) {
                        globalMin = price;
                        globalMinId = id;
                    }
                }
                double best = Double.MAX_VALUE;
                String bestId = null;
                for (String pid : PREFERRED) {
                    Double pr = prices.get(pid);
                    if (pr != null && pr < best) {
                        best = pr;
                        bestId = pid;
                    }
                }
                if (bestId != null) {
                    chosen = bestId;
                } else if (globalMinId != null) {
                    chosen = globalMinId;
                }
            }
        } catch (Exception ignore) {
            // 네트워크 실패 → 안전 기본값 사용
        }
        cachedModel = chosen;
        cachedAt = now;
        return chosen;
    }

    public String currentModel() {
        return cachedModel == null ? SAFE_DEFAULT : cachedModel;
    }

    /** 채팅 완성 호출. */
    public ChatResult chat(String model, String system, String user, int maxTokens) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        JsonArray msgs = new JsonArray();
        msgs.add(message("system", system));
        msgs.add(message("user", user));
        body.add("messages", msgs);
        body.addProperty("max_tokens", maxTokens);
        body.addProperty("temperature", 0.2);
        JsonObject usage = new JsonObject();
        usage.addProperty("include", true);   // 응답에 실제 비용(cost) 포함
        body.add("usage", usage);

        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/chat/completions"))
                .header("Authorization", "Bearer " + AppConfig.apiKey())
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "http://localhost:8080/farmterest")
                .header("X-Title", "Farmterest")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) {
            throw new RuntimeException("OpenRouter HTTP " + resp.statusCode() + ": " + truncate(resp.body(), 300));
        }
        JsonObject root = JsonParser.parseString(resp.body()).getAsJsonObject();
        ChatResult r = new ChatResult();
        r.model = model;
        r.content = root.getAsJsonArray("choices").get(0).getAsJsonObject()
                .getAsJsonObject("message").get("content").getAsString();
        if (root.has("usage") && root.get("usage").isJsonObject()) {
            JsonObject u = root.getAsJsonObject("usage");
            if (u.has("cost") && !u.get("cost").isJsonNull()) {
                r.cost = u.get("cost").getAsDouble();
            }
            if (u.has("prompt_tokens")) r.promptTokens = u.get("prompt_tokens").getAsInt();
            if (u.has("completion_tokens")) r.completionTokens = u.get("completion_tokens").getAsInt();
        }
        return r;
    }

    // ---- helpers ----
    private JsonObject message(String role, String content) {
        JsonObject m = new JsonObject();
        m.addProperty("role", role);
        m.addProperty("content", content);
        return m;
    }

    private double price(JsonObject model) {
        try {
            JsonObject pricing = model.getAsJsonObject("pricing");
            double p = pricing.has("prompt") ? Double.parseDouble(pricing.get("prompt").getAsString()) : 0;
            double c = pricing.has("completion") ? Double.parseDouble(pricing.get("completion").getAsString()) : 0;
            return p + c;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isTextModel(JsonObject model) {
        try {
            if (!model.has("architecture")) {
                return true;
            }
            JsonObject arch = model.getAsJsonObject("architecture");
            if (arch.has("output_modalities")) {
                for (JsonElement x : arch.getAsJsonArray("output_modalities")) {
                    if ("text".equals(x.getAsString())) {
                        return true;
                    }
                }
                return false;
            }
            if (arch.has("modality")) {
                return arch.get("modality").getAsString().contains("text");
            }
        } catch (Exception ignore) {
        }
        return true;
    }

    private String truncate(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n);
    }
}

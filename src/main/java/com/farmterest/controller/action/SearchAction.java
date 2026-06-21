package com.farmterest.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dao.SearchLogDAO;
import com.farmterest.model.dto.AiResult;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.PreferenceProfile;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.service.AiSearchService;
import com.farmterest.service.RecommendationService;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AI 맞춤검색.
 * 라이브 상품 카탈로그를 근거로 LLM(OpenRouter)이 필터·추천·설명을 생성하고,
 * 키가 없거나 예산 초과/오류면 자동으로 규칙기반(QueryParser)으로 폴백한다.
 */
public class SearchAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String q = Params.str(request, "q");
        ProductDAO productDAO = new ProductDAO();

        // 실시간 동기화 소스: 현재 DB의 전체 상품(신상품·재고·가격 변동 반영)
        List<ProductDTO> catalog = productDAO.search(new SearchCriteria());

        AiSearchService ai = new AiSearchService();
        AiResult aiRes = ai.enhance(q, catalog);
        SearchCriteria criteria = aiRes.getCriteria();

        List<ProductDTO> products = productDAO.search(criteria);

        // LLM이 고른 추천 상품이 있으면 그 순서를 앞세워 재정렬
        if (aiRes.isUsedLlm() && aiRes.getProductIds() != null && !aiRes.getProductIds().isEmpty()) {
            products = reorderByPicks(products, catalog, aiRes.getProductIds());
        }

        request.setAttribute("query", q == null ? "" : q);
        request.setAttribute("criteria", criteria);
        request.setAttribute("products", products);
        request.setAttribute("resultCount", products.size());

        // AI 추천 패널용 (비용/모델 표시는 제거 — 사용량은 OpenRouter 대시보드에서 확인)
        request.setAttribute("aiUsed", aiRes.isUsedLlm());
        request.setAttribute("aiExplanation", aiRes.getExplanation());

        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member != null) {
            // LLM이 이미 정렬했으면 취향 재정렬은 생략(중복 방지). 폴백일 때만 적용.
            if (!aiRes.isUsedLlm()) {
                RecommendationService rec = new RecommendationService();
                PreferenceProfile profile = rec.buildProfile(member.getMemberId());
                if (!profile.isEmpty()) {
                    // 검색 결과를 누락 없이 '재정렬'하고 취향 근거를 붙인다(걸러내기 X).
                    request.setAttribute("recommendations", rec.rankAll(profile, products));
                    request.setAttribute("profile", profile);
                }
            }
            new SearchLogDAO().insert(member.getMemberId(), q);
        } else if (q != null) {
            new SearchLogDAO().insert(null, q);
        }

        return ActionForward.forward("/WEB-INF/views/searchResult.jsp");
    }

    /** LLM 추천 id 순서를 앞에, 나머지 필터 결과를 뒤에 둔다. */
    private List<ProductDTO> reorderByPicks(List<ProductDTO> filtered, List<ProductDTO> catalog, List<Integer> picks) {
        Map<Integer, ProductDTO> catById = new HashMap<>();
        for (ProductDTO p : catalog) {
            catById.put(p.getProductId(), p);
        }
        LinkedHashMap<Integer, ProductDTO> ordered = new LinkedHashMap<>();
        for (Integer id : picks) {
            ProductDTO p = catById.get(id);
            if (p != null) {
                ordered.put(id, p);
            }
        }
        for (ProductDTO p : filtered) {
            ordered.putIfAbsent(p.getProductId(), p);
        }
        return new ArrayList<>(ordered.values());
    }
}

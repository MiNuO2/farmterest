package com.farmterest.controller.action;

import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;
import com.farmterest.service.RecommendationService;
import com.farmterest.service.SeasonService;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 메인 화면: 인기 상품 + (로그인 시) 맞춤 추천. */
public class MainAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProductDAO productDAO = new ProductDAO();

        List<ProductDTO> popular = productDAO.findPopular(8);
        request.setAttribute("popularProducts", popular);

        // 품목 바로가기 버튼 + 오늘의 제철 강조(날짜 기반)
        request.setAttribute("seasonalMap", SeasonService.seasonalFlags());

        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member != null) {
            List<ProductDTO> all = productDAO.search(new SearchCriteria());
            List<Recommendation> recs =
                    new RecommendationService().recommend(member.getMemberId(), all, 4);
            request.setAttribute("recommendations", recs);
        }

        return ActionForward.forward("/WEB-INF/views/main.jsp");
    }
}

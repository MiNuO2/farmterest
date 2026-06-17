package com.farmterest.controller.action;

import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.OrderDAO;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dao.SearchLogDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.OrderDTO;
import com.farmterest.model.dto.PreferenceProfile;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;
import com.farmterest.service.RecommendationService;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 마이페이지: 내 정보 · 구매이력 · 선호도 대시보드 · 맞춤추천. */
public class MyPageAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        int memberId = member.getMemberId();
        List<OrderDTO> orders = new OrderDAO().findByMember(memberId);
        request.setAttribute("orders", orders);

        RecommendationService rec = new RecommendationService();
        PreferenceProfile profile = rec.buildProfile(memberId);
        request.setAttribute("profile", profile);

        List<String> recentSearches = new SearchLogDAO().findRecentByMember(memberId, 5);
        request.setAttribute("recentSearches", recentSearches);

        if (!profile.isEmpty()) {
            List<ProductDTO> all = new ProductDAO().search(new SearchCriteria());
            List<Recommendation> recs = rec.rank(profile, all, 4);
            request.setAttribute("recommendations", recs);
        }

        return ActionForward.forward("/WEB-INF/views/mypage.jsp");
    }
}

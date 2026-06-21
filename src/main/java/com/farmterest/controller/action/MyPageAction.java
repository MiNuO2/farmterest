package com.farmterest.controller.action;

import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.OrderDAO;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dao.ReviewDAO;
import com.farmterest.model.dao.SearchLogDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.OrderDTO;
import com.farmterest.model.dto.PreferenceProfile;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;
import com.farmterest.model.dto.SellerStats;
import com.farmterest.service.RecommendationService;
import com.farmterest.service.SellerDashboardService;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

        // 판매자는 소비자용 마이페이지 대신 '판매 대시보드'로
        if (member.isSeller()) {
            return sellerDashboard(request, member);
        }

        int memberId = member.getMemberId();
        List<OrderDTO> orders = new OrderDAO().findByMember(memberId);
        request.setAttribute("orders", orders);

        // 주문항목ID → 내가 남긴 후기 (있으면 별점 표시, 없으면 작성 폼)
        request.setAttribute("myReviews", new ReviewDAO().findByMemberItemMap(memberId));

        RecommendationService rec = new RecommendationService();
        PreferenceProfile profile = rec.buildProfile(memberId);
        request.setAttribute("profile", profile);

        List<String> recentSearches = new SearchLogDAO().findRecentByMember(memberId, 5);
        request.setAttribute("recentSearches", recentSearches);

        List<ProductDTO> all = new ProductDAO().search(new SearchCriteria());
        if (!profile.isEmpty()) {
            request.setAttribute("recommendations", rec.rank(profile, all, 4));
        } else {
            // 구매이력이 없어도 맞춤추천 영역이 비지 않도록 신뢰기반(별점·품질) 추천
            request.setAttribute("recommendations", rec.coldStart(all, 4));
        }

        return ActionForward.forward("/WEB-INF/views/mypage.jsp");
    }

    /** 판매자 대시보드: 내 상품 판매·매출·재고·평점 집계 + 받은 후기. */
    private ActionForward sellerDashboard(HttpServletRequest request, MemberDTO member) throws Exception {
        String yyyymm = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<ProductDTO> products = new ProductDAO().findBySellerWithStats(member.getMemberId(), yyyymm);
        SellerStats stats = new SellerDashboardService().summarize(products);
        request.setAttribute("products", products);
        request.setAttribute("stats", stats);
        request.setAttribute("recentReviews", new ReviewDAO().findBySellerProducts(member.getMemberId(), 6));
        return ActionForward.forward("/WEB-INF/views/sellerHome.jsp");
    }
}

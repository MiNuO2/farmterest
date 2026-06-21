package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ReviewDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.ReviewDTO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 별점/후기 저장: 본인이 구매한 주문항목에 대해서만 허용(소유권 검증). */
public class ReviewSaveAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        int orderItemId = Params.intOr(request, "orderItemId", 0);
        int rating = Params.intOr(request, "rating", 0);
        String comment = Params.str(request, "comment");

        String mypage = request.getContextPath() + "/mypage.do";
        if (rating < 1 || rating > 5 || orderItemId <= 0) {
            return ActionForward.redirect(mypage);
        }

        ReviewDAO dao = new ReviewDAO();
        // 본인 구매가 맞는지 확인하고 그 상품 ID를 얻는다(위조 방지).
        int productId = dao.ownedProductId(member.getMemberId(), orderItemId);
        if (productId <= 0) {
            return ActionForward.redirect(mypage);
        }

        ReviewDTO r = new ReviewDTO();
        r.setProductId(productId);
        r.setMemberId(member.getMemberId());
        r.setOrderItemId(orderItemId);
        r.setRating(rating);
        r.setComment(comment);
        dao.save(r);

        return ActionForward.redirect(mypage);
    }
}

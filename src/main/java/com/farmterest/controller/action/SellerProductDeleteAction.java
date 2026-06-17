package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 판매자: 본인 상품 삭제. */
public class SellerProductDeleteAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null || !member.isSeller()) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }
        int id = Params.intOr(request, "id", 0);
        if (id > 0) {
            new ProductDAO().delete(id, member.getMemberId());
        }
        return ActionForward.redirect(request.getContextPath() + "/sellerProducts.do");
    }
}

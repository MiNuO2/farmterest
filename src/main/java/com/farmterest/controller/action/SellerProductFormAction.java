package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 판매자: 상품 등록/수정 폼. id 있으면 수정 모드. */
public class SellerProductFormAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null || !member.isSeller()) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        int id = Params.intOr(request, "id", 0);
        if (id > 0) {
            ProductDTO product = new ProductDAO().findById(id);
            // 본인 상품만 수정 가능
            if (product != null && product.getSellerId() == member.getMemberId()) {
                request.setAttribute("product", product);
            }
        }
        return ActionForward.forward("/WEB-INF/views/sellerProductForm.jsp");
    }
}

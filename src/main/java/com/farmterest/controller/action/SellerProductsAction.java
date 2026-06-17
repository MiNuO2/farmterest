package com.farmterest.controller.action;

import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 판매자: 내 상품 목록. */
public class SellerProductsAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null || !member.isSeller()) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }
        List<ProductDTO> products = new ProductDAO().findBySeller(member.getMemberId());
        request.setAttribute("products", products);
        return ActionForward.forward("/WEB-INF/views/sellerProducts.jsp");
    }
}

package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 장바구니 보기. */
public class CartViewAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("cartItems", CartService.getCart(request.getSession()));
        request.setAttribute("cartTotal", CartService.total(request.getSession()));
        return ActionForward.forward("/WEB-INF/views/cart.jsp");
    }
}

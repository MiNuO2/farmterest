package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.service.CartService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 장바구니 항목 삭제. */
public class CartRemoveAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Params.intOr(request, "id", 0);
        CartService.remove(request.getSession(), id);
        return ActionForward.redirect(request.getContextPath() + "/cart.do");
    }
}

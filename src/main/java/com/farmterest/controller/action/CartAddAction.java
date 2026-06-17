package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.CartItem;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.service.CartService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 장바구니 담기. */
public class CartAddAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Params.intOr(request, "id", 0);
        int qty = Params.intOr(request, "qty", 1);
        if (qty < 1) {
            qty = 1;
        }
        ProductDTO p = new ProductDAO().findById(id);
        if (p != null) {
            CartService.add(request.getSession(),
                    new CartItem(p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), qty));
        }
        return ActionForward.redirect(request.getContextPath() + "/cart.do");
    }
}

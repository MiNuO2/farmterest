package com.farmterest.controller.action;

import java.util.ArrayList;
import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.OrderDAO;
import com.farmterest.model.dto.CartItem;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.OrderDTO;
import com.farmterest.model.dto.OrderItemDTO;
import com.farmterest.service.CartService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 주문 생성: 세션 장바구니 → DB 저장(트랜잭션) → 장바구니 비움. */
public class OrderAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        List<CartItem> cart = CartService.getCart(request.getSession());
        if (cart.isEmpty()) {
            return ActionForward.redirect(request.getContextPath() + "/cart.do");
        }

        OrderDTO order = new OrderDTO();
        order.setMemberId(member.getMemberId());
        order.setStatus("PAID");
        int total = 0;
        List<OrderItemDTO> items = new ArrayList<>();
        for (CartItem c : cart) {
            OrderItemDTO it = new OrderItemDTO();
            it.setProductId(c.getProductId());
            it.setProductName(c.getProductName());
            it.setQty(c.getQty());
            it.setUnitPrice(c.getPrice());
            items.add(it);
            total += c.getSubtotal();
        }
        order.setItems(items);
        order.setTotalPrice(total);

        int orderId = new OrderDAO().insertOrder(order);

        // 완료 화면용 스냅샷 후 장바구니 비움
        request.setAttribute("orderId", orderId);
        request.setAttribute("orderedItems", items);
        request.setAttribute("orderTotal", total);
        CartService.clear(request.getSession());

        return ActionForward.forward("/WEB-INF/views/orderDone.jsp");
    }
}

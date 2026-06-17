package com.farmterest.service;

import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dto.CartItem;

import jakarta.servlet.http.HttpSession;

/** 세션 기반 장바구니. (HTTP 비연결성을 세션으로 보완) */
public class CartService {

    public static final String CART = "cart";

    @SuppressWarnings("unchecked")
    public static List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART, cart);
        }
        return cart;
    }

    /** 같은 상품이면 수량 누적, 아니면 추가. */
    public static void add(HttpSession session, CartItem item) {
        List<CartItem> cart = getCart(session);
        for (CartItem c : cart) {
            if (c.getProductId() == item.getProductId()) {
                c.setQty(c.getQty() + item.getQty());
                return;
            }
        }
        cart.add(item);
    }

    public static void remove(HttpSession session, int productId) {
        getCart(session).removeIf(c -> c.getProductId() == productId);
    }

    public static int total(HttpSession session) {
        int sum = 0;
        for (CartItem c : getCart(session)) {
            sum += c.getSubtotal();
        }
        return sum;
    }

    public static int count(HttpSession session) {
        return getCart(session).size();
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(CART);
    }
}

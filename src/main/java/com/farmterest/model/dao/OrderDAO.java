package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dto.OrderDTO;
import com.farmterest.model.dto.OrderItemDTO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.DBManager;

/** 주문 영속 처리. 주문+상세는 한 트랜잭션으로 저장. */
public class OrderDAO {

    /** 주문 헤더 + 상세 항목을 트랜잭션으로 저장하고 order_id 반환. */
    public int insertOrder(OrderDTO order) throws Exception {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);

            int orderId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders (member_id, total_price, status) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getMemberId());
                ps.setInt(2, order.getTotalPrice());
                ps.setString(3, order.getStatus() == null ? "PAID" : order.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    orderId = keys.next() ? keys.getInt(1) : 0;
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO order_item (order_id, product_id, qty, unit_price) VALUES (?,?,?,?)")) {
                for (OrderItemDTO item : order.getItems()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQty());
                    ps.setInt(4, item.getUnitPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 재고 차감
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE product SET stock = stock - ? WHERE product_id = ?")) {
                for (OrderItemDTO item : order.getItems()) {
                    ps.setInt(1, item.getQty());
                    ps.setInt(2, item.getProductId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return orderId;
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ignore) {}
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignore) {}
                DBManager.close(con);
            }
        }
    }

    /** 회원의 주문 목록(상세 포함, 최신순). */
    public List<OrderDTO> findByMember(int memberId) throws Exception {
        List<OrderDTO> orders = new ArrayList<>();
        try (Connection con = DBManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM orders WHERE member_id = ? ORDER BY ordered_at DESC")) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        OrderDTO o = new OrderDTO();
                        o.setOrderId(rs.getInt("order_id"));
                        o.setMemberId(rs.getInt("member_id"));
                        o.setOrderedAt(rs.getTimestamp("ordered_at"));
                        o.setTotalPrice(rs.getInt("total_price"));
                        o.setStatus(rs.getString("status"));
                        orders.add(o);
                    }
                }
            }
            // 각 주문의 상세
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT oi.*, p.name AS product_name FROM order_item oi "
                    + "JOIN product p ON oi.product_id = p.product_id WHERE oi.order_id = ?")) {
                for (OrderDTO o : orders) {
                    ps.setInt(1, o.getOrderId());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            OrderItemDTO it = new OrderItemDTO();
                            it.setOrderItemId(rs.getInt("order_item_id"));
                            it.setOrderId(rs.getInt("order_id"));
                            it.setProductId(rs.getInt("product_id"));
                            it.setProductName(rs.getString("product_name"));
                            it.setQty(rs.getInt("qty"));
                            it.setUnitPrice(rs.getInt("unit_price"));
                            o.getItems().add(it);
                        }
                    }
                }
            }
        }
        return orders;
    }

    /** 회원이 구매한 상품 목록(품질지표 포함) — 추천 프로필 생성용. */
    public List<ProductDTO> findPurchasedProducts(int memberId) throws Exception {
        String sql = "SELECT DISTINCT p.*, m.name AS seller_name FROM product p "
                + "JOIN order_item oi ON oi.product_id = p.product_id "
                + "JOIN orders o ON o.order_id = oi.order_id "
                + "JOIN member m ON p.seller_id = m.member_id "
                + "WHERE o.member_id = ?";
        List<ProductDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(ProductDAO.mapProduct(rs));
                }
            }
        }
        return list;
    }
}

package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.farmterest.model.dto.ReviewDTO;
import com.farmterest.util.DBManager;

/** 상품 후기/별점 영속 처리. */
public class ReviewDAO {

    private static ReviewDTO mapReview(ResultSet rs) throws Exception {
        ReviewDTO r = new ReviewDTO();
        r.setReviewId(rs.getInt("review_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setMemberId(rs.getInt("member_id"));
        r.setOrderItemId((Integer) rs.getObject("order_item_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            r.setMemberName(rs.getString("member_name"));
        } catch (Exception ignore) {
            // member 조인이 없는 조회면 생략
        }
        return r;
    }

    /**
     * 주문항목이 해당 회원의 구매가 맞는지 검증하고, 맞으면 그 상품 ID를 반환.
     * 본인 구매가 아니면 0 (후기 작성 거부 근거).
     */
    public int ownedProductId(int memberId, int orderItemId) throws Exception {
        String sql = "SELECT oi.product_id FROM order_item oi "
                + "JOIN orders o ON o.order_id = oi.order_id "
                + "WHERE oi.order_item_id = ? AND o.member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderItemId);
            ps.setInt(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** 후기 저장 (같은 주문항목에 이미 있으면 별점/코멘트 수정). */
    public void save(ReviewDTO r) throws Exception {
        String sql = "INSERT INTO review (product_id, member_id, order_item_id, rating, comment) "
                + "VALUES (?,?,?,?,?) AS nw "
                + "ON DUPLICATE KEY UPDATE rating = nw.rating, comment = nw.comment";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getProductId());
            ps.setInt(2, r.getMemberId());
            if (r.getOrderItemId() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, r.getOrderItemId());
            }
            ps.setInt(4, r.getRating());
            ps.setString(5, r.getComment());
            ps.executeUpdate();
        }
    }

    /** 상품 상세의 후기 목록(최신순, 작성자 이름 포함). */
    public List<ReviewDTO> findByProduct(int productId) throws Exception {
        String sql = "SELECT rv.*, m.name AS member_name FROM review rv "
                + "JOIN member m ON m.member_id = rv.member_id "
                + "WHERE rv.product_id = ? ORDER BY rv.created_at DESC";
        List<ReviewDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReview(rs));
                }
            }
        }
        return list;
    }

    /** 판매자의 상품들에 달린 최근 후기(상품명·작성자 포함) — 판매자 대시보드용. */
    public List<ReviewDTO> findBySellerProducts(int sellerId, int limit) throws Exception {
        String sql = "SELECT rv.*, m.name AS member_name, p.name AS product_name "
                + "FROM review rv "
                + "JOIN member m ON m.member_id = rv.member_id "
                + "JOIN product p ON p.product_id = rv.product_id "
                + "WHERE p.seller_id = ? ORDER BY rv.created_at DESC LIMIT ?";
        List<ReviewDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO r = mapReview(rs);
                    try {
                        r.setProductName(rs.getString("product_name"));
                    } catch (Exception ignore) {
                        // product_name 없는 조회면 생략
                    }
                    list.add(r);
                }
            }
        }
        return list;
    }

    /** 회원이 남긴 후기를 '주문항목ID → 후기' 맵으로 (마이페이지에서 작성여부 표시용). */
    public Map<Integer, ReviewDTO> findByMemberItemMap(int memberId) throws Exception {
        Map<Integer, ReviewDTO> map = new LinkedHashMap<>();
        String sql = "SELECT rv.* FROM review rv WHERE rv.member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewDTO r = mapReview(rs);
                    if (r.getOrderItemId() != null) {
                        map.put(r.getOrderItemId(), r);
                    }
                }
            }
        }
        return map;
    }
}

package com.farmterest.model.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.DBManager;
import com.farmterest.util.SearchCriteria;

/** 상품 영속 처리. 핵심은 search() 의 동적 SQL. */
public class ProductDAO {

    /** ResultSet → ProductDTO 매핑 (다른 DAO 에서도 재사용). */
    public static ProductDTO mapProduct(ResultSet rs) throws Exception {
        ProductDTO p = new ProductDTO();
        p.setProductId(rs.getInt("product_id"));
        p.setSellerId(rs.getInt("seller_id"));
        try {
            p.setSellerName(rs.getString("seller_name"));
        } catch (Exception ignore) {
            // seller_name 컬럼이 없는 조회면 생략
        }
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setRegion(rs.getString("region"));
        p.setPrice(rs.getInt("price"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        p.setDescription(rs.getString("description"));
        p.setPolishedRate((Integer) rs.getObject("polished_rate"));
        p.setWholeGrainRate((Integer) rs.getObject("whole_grain_rate"));
        BigDecimal m = rs.getBigDecimal("moisture");
        p.setMoisture(m == null ? null : m.doubleValue());
        p.setTasteScore((Integer) rs.getObject("taste_score"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    private static final String BASE_SELECT =
            "SELECT p.*, m.name AS seller_name FROM product p "
            + "JOIN member m ON p.seller_id = m.member_id ";

    /** 동적 SQL 검색: 채워진 조건만 WHERE/ORDER BY 로 조립. */
    public List<ProductDTO> search(SearchCriteria c) throws Exception {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            sql.append("AND (p.name LIKE ? OR p.description LIKE ?) ");
            String like = "%" + c.getKeyword().trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (c.getCategory() != null) {
            sql.append("AND p.category = ? ");
            params.add(c.getCategory());
        }
        if (c.getRegion() != null) {
            sql.append("AND p.region = ? ");
            params.add(c.getRegion());
        }
        if (c.getPriceMin() != null) {
            sql.append("AND p.price >= ? ");
            params.add(c.getPriceMin());
        }
        if (c.getPriceMax() != null) {
            sql.append("AND p.price <= ? ");
            params.add(c.getPriceMax());
        }
        if (c.getMinPolishedRate() != null) {
            sql.append("AND p.polished_rate >= ? ");
            params.add(c.getMinPolishedRate());
        }
        if (c.getMinWholeGrainRate() != null) {
            sql.append("AND p.whole_grain_rate >= ? ");
            params.add(c.getMinWholeGrainRate());
        }
        if (c.getMinTasteScore() != null) {
            sql.append("AND p.taste_score >= ? ");
            params.add(c.getMinTasteScore());
        }

        sql.append(orderByClause(c.getSort()));

        List<ProductDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    private String orderByClause(String sort) {
        if (sort == null) {
            return "ORDER BY p.created_at DESC ";
        }
        switch (sort) {
            case SearchCriteria.SORT_PRICE_ASC:  return "ORDER BY p.price ASC ";
            case SearchCriteria.SORT_PRICE_DESC: return "ORDER BY p.price DESC ";
            case SearchCriteria.SORT_QUALITY:    return "ORDER BY p.taste_score DESC, p.polished_rate DESC ";
            case SearchCriteria.SORT_NEWEST:     return "ORDER BY p.created_at DESC, p.product_id DESC ";
            default:                             return "ORDER BY p.created_at DESC ";
        }
    }

    public ProductDTO findById(int productId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(BASE_SELECT + "WHERE p.product_id = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapProduct(rs) : null;
            }
        }
    }

    public List<ProductDTO> findBySeller(int sellerId) throws Exception {
        List<ProductDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     BASE_SELECT + "WHERE p.seller_id = ? ORDER BY p.created_at DESC")) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    /** 판매량 기준 인기 상품 (없으면 최신순). */
    public List<ProductDTO> findPopular(int limit) throws Exception {
        String sql = "SELECT p.*, m.name AS seller_name, COALESCE(SUM(oi.qty),0) AS sold "
                + "FROM product p JOIN member m ON p.seller_id = m.member_id "
                + "LEFT JOIN order_item oi ON oi.product_id = p.product_id "
                + "GROUP BY p.product_id ORDER BY sold DESC, p.created_at DESC LIMIT ?";
        List<ProductDTO> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        }
        return list;
    }

    public int insert(ProductDTO p) throws Exception {
        String sql = "INSERT INTO product "
                + "(seller_id, name, category, region, price, stock, image_url, description, "
                + " polished_rate, whole_grain_rate, moisture, taste_score) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getSellerId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setString(4, p.getRegion());
            ps.setInt(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getImageUrl());
            ps.setString(8, p.getDescription());
            setNullableInt(ps, 9, p.getPolishedRate());
            setNullableInt(ps, 10, p.getWholeGrainRate());
            if (p.getMoisture() == null) {
                ps.setNull(11, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(11, BigDecimal.valueOf(p.getMoisture()));
            }
            setNullableInt(ps, 12, p.getTasteScore());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(ProductDTO p) throws Exception {
        String sql = "UPDATE product SET name=?, category=?, region=?, price=?, stock=?, "
                + "image_url=?, description=?, polished_rate=?, whole_grain_rate=?, moisture=?, taste_score=? "
                + "WHERE product_id=? AND seller_id=?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setString(3, p.getRegion());
            ps.setInt(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getImageUrl());
            ps.setString(7, p.getDescription());
            setNullableInt(ps, 8, p.getPolishedRate());
            setNullableInt(ps, 9, p.getWholeGrainRate());
            if (p.getMoisture() == null) {
                ps.setNull(10, java.sql.Types.DECIMAL);
            } else {
                ps.setBigDecimal(10, BigDecimal.valueOf(p.getMoisture()));
            }
            setNullableInt(ps, 11, p.getTasteScore());
            ps.setInt(12, p.getProductId());
            ps.setInt(13, p.getSellerId());
            return ps.executeUpdate() > 0;
        }
    }

    /** 본인 상품만 삭제 (seller_id 검사). */
    public boolean delete(int productId, int sellerId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM product WHERE product_id=? AND seller_id=?")) {
            ps.setInt(1, productId);
            ps.setInt(2, sellerId);
            return ps.executeUpdate() > 0;
        }
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer value) throws Exception {
        if (value == null) {
            ps.setNull(idx, java.sql.Types.INTEGER);
        } else {
            ps.setInt(idx, value);
        }
    }
}

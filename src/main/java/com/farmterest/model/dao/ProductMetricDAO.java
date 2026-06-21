package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dto.MetricDefinition;
import com.farmterest.model.dto.ProductMetric;
import com.farmterest.util.DBManager;

/** 상품별 지표 값 영속 처리. */
public class ProductMetricDAO {

    /** 상품의 지표 값 목록(정의 조인, 거부됨 제외, 정렬순). */
    public List<ProductMetric> findByProduct(int productId) throws Exception {
        String sql = "SELECT pm.pm_id, pm.product_id, pm.value, pm.sort_order, "
                + "d.def_id, d.metric_key, d.label, d.unit, d.category, d.status, d.good_high, "
                + "d.gauge_min, d.gauge_max, d.help_summary, d.help_body, d.created_by "
                + "FROM product_metric pm JOIN metric_definition d ON d.def_id = pm.def_id "
                + "WHERE pm.product_id = ? AND d.status <> 'REJECTED' "
                + "ORDER BY pm.sort_order, pm.pm_id";
        List<ProductMetric> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductMetric m = new ProductMetric();
                    m.setPmId(rs.getInt("pm_id"));
                    m.setProductId(rs.getInt("product_id"));
                    m.setValue(rs.getString("value"));
                    m.setSortOrder(rs.getInt("sort_order"));
                    MetricDefinition d = MetricDefinitionDAO.map(rs);
                    m.setDef(d);
                    list.add(m);
                }
            }
        }
        return list;
    }

    /** 상품 지표를 통째로 교체(저장 시). 각 항목은 def_id + value + sort_order. */
    public void replaceForProduct(int productId, List<ProductMetric> metrics) throws Exception {
        Connection con = null;
        try {
            con = DBManager.getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement del = con.prepareStatement("DELETE FROM product_metric WHERE product_id = ?")) {
                del.setInt(1, productId);
                del.executeUpdate();
            }
            if (metrics != null && !metrics.isEmpty()) {
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO product_metric (product_id, def_id, value, sort_order) VALUES (?,?,?,?)")) {
                    int order = 0;
                    for (ProductMetric m : metrics) {
                        ins.setInt(1, productId);
                        ins.setInt(2, m.getDef().getDefId());
                        ins.setString(3, m.getValue());
                        ins.setInt(4, order++);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }
            con.commit();
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignore) {}
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignore) {}
                DBManager.close(con);
            }
        }
    }
}

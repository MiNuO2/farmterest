package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.model.dto.MetricDefinition;
import com.farmterest.util.DBManager;

/** 지표 정의 영속 처리(카탈로그/제안/승인). */
public class MetricDefinitionDAO {

    static MetricDefinition map(ResultSet rs) throws Exception {
        MetricDefinition d = new MetricDefinition();
        d.setDefId(rs.getInt("def_id"));
        d.setMetricKey(rs.getString("metric_key"));
        d.setLabel(rs.getString("label"));
        d.setUnit(rs.getString("unit"));
        d.setCategory(rs.getString("category"));
        d.setStatus(rs.getString("status"));
        d.setGoodHigh(rs.getInt("good_high") == 1);
        java.math.BigDecimal mn = rs.getBigDecimal("gauge_min");
        java.math.BigDecimal mx = rs.getBigDecimal("gauge_max");
        d.setGaugeMin(mn == null ? null : mn.doubleValue());
        d.setGaugeMax(mx == null ? null : mx.doubleValue());
        d.setHelpSummary(rs.getString("help_summary"));
        d.setHelpBody(rs.getString("help_body"));
        d.setCreatedBy(rs.getInt("created_by"));
        return d;
    }

    private static final String SELECT_ALL =
            "SELECT def_id, metric_key, label, unit, category, status, good_high, gauge_min, gauge_max, "
            + "help_summary, help_body, created_by FROM metric_definition ";

    /** 판매자 등록폼 추천 지표: 해당 품목의 공식(카탈로그/승인) 지표. */
    public List<MetricDefinition> findSuggestions(String category) throws Exception {
        List<MetricDefinition> list = new ArrayList<>();
        String sql = SELECT_ALL + "WHERE status IN ('CATALOG','APPROVED') AND (category = ? OR category IS NULL) "
                + "ORDER BY (category IS NULL), def_id";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public MetricDefinition findById(int defId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL + "WHERE def_id = ?")) {
            ps.setInt(1, defId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** 관리자 화면: 상태별 목록(PENDING 먼저). */
    public List<MetricDefinition> findAll() throws Exception {
        List<MetricDefinition> list = new ArrayList<>();
        String sql = SELECT_ALL + "ORDER BY FIELD(status,'PENDING','APPROVED','CATALOG','REJECTED'), def_id DESC";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * 커스텀 지표를 찾거나 새로 PENDING 생성하고 def_id 반환.
     * - 키는 '품목+라벨'로 정규화해 의미가 다른 같은 라벨(예: 감자 '크기' vs 수산 '크기')을 분리.
     * - 키 길이는 컬럼(VARCHAR 60)에 맞춰 클램프.
     * - 거부(REJECTED)된 라벨을 다시 등록하면 PENDING으로 되살려(다시 검토) 값이 증발하지 않게 함.
     * - 동시성: 중복키로 INSERT가 실패하면 재조회해 멱등 처리.
     */
    public int findOrCreateCustom(String label, String unit, String category, int createdBy) throws Exception {
        String safeLabel = label.trim();
        if (safeLabel.length() > 60) safeLabel = safeLabel.substring(0, 60);
        String catPart = (category == null || category.isBlank()) ? "" : category.trim() + "_";
        String key = "c_" + catPart + safeLabel.toLowerCase().replaceAll("\\s+", "_");
        if (key.length() > 60) key = key.substring(0, 60);

        try (Connection con = DBManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT def_id, status FROM metric_definition WHERE metric_key = ?")) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        if ("REJECTED".equals(rs.getString(2))) {
                            try (PreparedStatement up = con.prepareStatement(
                                    "UPDATE metric_definition SET status='PENDING' WHERE def_id=?")) {
                                up.setInt(1, id);
                                up.executeUpdate();
                            }
                        }
                        return id;
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO metric_definition (metric_key, label, unit, category, status, good_high, created_by) "
                    + "VALUES (?,?,?,?,'PENDING',1,?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, key);
                ps.setString(2, safeLabel);
                ps.setString(3, unit);
                ps.setString(4, (category == null || category.isBlank()) ? null : category.trim());
                ps.setInt(5, createdBy);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : 0;
                }
            } catch (java.sql.SQLException dup) {
                // 동시 요청이 먼저 INSERT한 경우 → 재조회
                try (PreparedStatement ps = con.prepareStatement("SELECT def_id FROM metric_definition WHERE metric_key = ?")) {
                    ps.setString(1, key);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getInt(1);
                    }
                }
                throw dup;
            }
        }
    }

    /** 관리자 승인: 공식 지표로 전환하고 도움말 부착. */
    public boolean approve(int defId, String helpSummary, String helpBody, boolean goodHigh,
                           Double gaugeMin, Double gaugeMax) throws Exception {
        String sql = "UPDATE metric_definition SET status='APPROVED', help_summary=?, help_body=?, "
                + "good_high=?, gauge_min=?, gauge_max=? WHERE def_id=?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, helpSummary);
            ps.setString(2, helpBody);
            ps.setInt(3, goodHigh ? 1 : 0);
            if (gaugeMin == null) ps.setNull(4, java.sql.Types.DECIMAL); else ps.setBigDecimal(4, java.math.BigDecimal.valueOf(gaugeMin));
            if (gaugeMax == null) ps.setNull(5, java.sql.Types.DECIMAL); else ps.setBigDecimal(5, java.math.BigDecimal.valueOf(gaugeMax));
            ps.setInt(6, defId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean reject(int defId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE metric_definition SET status='REJECTED' WHERE def_id=?")) {
            ps.setInt(1, defId);
            return ps.executeUpdate() > 0;
        }
    }
}

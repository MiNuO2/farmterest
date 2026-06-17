package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.farmterest.util.DBManager;

/** 검색 로그 영속 처리 (맞춤 추천/통계 데이터). */
public class SearchLogDAO {

    public void insert(Integer memberId, String query) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO search_log (member_id, query_text) VALUES (?,?)")) {
            if (memberId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, memberId);
            }
            ps.setString(2, query);
            ps.executeUpdate();
        }
    }

    public List<String> findRecentByMember(int memberId, int limit) throws Exception {
        List<String> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT query_text FROM search_log WHERE member_id = ? "
                     + "ORDER BY searched_at DESC LIMIT ?")) {
            ps.setInt(1, memberId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("query_text"));
                }
            }
        }
        return list;
    }

    /** 인기 검색어: 검색 로그를 집계해 많이 검색된 순으로(동률은 최신순). */
    public List<String> popularKeywords(int limit) throws Exception {
        String sql = "SELECT query_text FROM search_log "
                + "WHERE query_text IS NOT NULL AND TRIM(query_text) <> '' "
                + "GROUP BY query_text "
                + "ORDER BY COUNT(*) DESC, MAX(searched_at) DESC "
                + "LIMIT ?";
        List<String> list = new ArrayList<>();
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("query_text"));
                }
            }
        }
        return list;
    }
}

package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.farmterest.util.DBManager;

/** LLM 사용 비용을 월 단위로 누적/조회 (예산 차단 근거). */
public class AiUsageDAO {

    /** 해당 월(yyyymm) 누적 비용(USD). 없으면 0. */
    public double monthCost(String yyyymm) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT cost_usd FROM ai_usage WHERE yyyymm = ?")) {
            ps.setString(1, yyyymm);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    /** 호출 1건의 비용을 누적(월 행 upsert). */
    public void addUsage(String yyyymm, double costUsd) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO ai_usage (yyyymm, cost_usd, calls) VALUES (?, ?, 1) "
                     + "ON DUPLICATE KEY UPDATE cost_usd = cost_usd + VALUES(cost_usd), calls = calls + 1")) {
            ps.setString(1, yyyymm);
            ps.setDouble(2, costUsd);
            ps.executeUpdate();
        }
    }

    public int monthCalls(String yyyymm) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT calls FROM ai_usage WHERE yyyymm = ?")) {
            ps.setString(1, yyyymm);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}

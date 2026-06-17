package com.farmterest.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.farmterest.model.dto.MemberDTO;
import com.farmterest.util.DBManager;

/** 회원 영속 처리. */
public class MemberDAO {

    private MemberDTO map(ResultSet rs) throws Exception {
        MemberDTO m = new MemberDTO();
        m.setMemberId(rs.getInt("member_id"));
        m.setLoginId(rs.getString("login_id"));
        m.setPassword(rs.getString("password"));
        m.setName(rs.getString("name"));
        m.setRole(rs.getString("role"));
        m.setRegion(rs.getString("region"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        return m;
    }

    public MemberDTO findByLoginId(String loginId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM member WHERE login_id = ?")) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public MemberDTO findById(int memberId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM member WHERE member_id = ?")) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean existsLoginId(String loginId) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT 1 FROM member WHERE login_id = ?")) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int insert(MemberDTO m) throws Exception {
        try (Connection con = DBManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO member (login_id, password, name, role, region) VALUES (?,?,?,?,?)")) {
            ps.setString(1, m.getLoginId());
            ps.setString(2, m.getPassword());
            ps.setString(3, m.getName());
            ps.setString(4, m.getRole());
            ps.setString(5, m.getRegion());
            return ps.executeUpdate();
        }
    }
}

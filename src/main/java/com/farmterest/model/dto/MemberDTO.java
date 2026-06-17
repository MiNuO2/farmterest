package com.farmterest.model.dto;

import java.sql.Timestamp;

/** 회원 자바빈 (소비자/판매자). */
public class MemberDTO {

    private int memberId;
    private String loginId;
    private String password;
    private String name;
    private String role;     // CONSUMER | SELLER
    private String region;
    private Timestamp createdAt;

    public MemberDTO() {
    }

    public boolean isSeller() {
        return "SELLER".equals(role);
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

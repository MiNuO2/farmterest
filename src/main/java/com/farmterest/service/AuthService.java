package com.farmterest.service;

import com.farmterest.model.dao.MemberDAO;
import com.farmterest.model.dto.MemberDTO;

/** 로그인/회원가입 인증 로직. */
public class AuthService {

    private final MemberDAO memberDAO = new MemberDAO();

    /** 성공 시 회원, 실패 시 null. */
    public MemberDTO login(String loginId, String password) throws Exception {
        MemberDTO m = memberDAO.findByLoginId(loginId);
        if (m != null && m.getPassword().equals(password)) {
            return m;
        }
        return null;
    }

    /** 아이디 중복이면 false, 가입 성공이면 true. */
    public boolean join(MemberDTO member) throws Exception {
        if (memberDAO.existsLoginId(member.getLoginId())) {
            return false;
        }
        if (member.getRole() == null) {
            member.setRole("CONSUMER");
        }
        return memberDAO.insert(member) > 0;
    }

    public boolean isLoginIdTaken(String loginId) throws Exception {
        return memberDAO.existsLoginId(loginId);
    }
}

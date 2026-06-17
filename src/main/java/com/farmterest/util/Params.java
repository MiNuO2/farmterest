package com.farmterest.util;

import jakarta.servlet.http.HttpServletRequest;

/** 요청 파라미터 안전 추출 유틸. */
public class Params {

    /** 세션에 보관하는 로그인 회원 키. */
    public static final String LOGIN = "loginMember";

    /** 빈 문자열이면 null 반환. */
    public static String str(HttpServletRequest r, String name) {
        String v = r.getParameter(name);
        if (v == null) {
            return null;
        }
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    public static int intOr(HttpServletRequest r, String name, int def) {
        try {
            return Integer.parseInt(r.getParameter(name).trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer intObj(HttpServletRequest r, String name) {
        try {
            String v = r.getParameter(name);
            if (v == null || v.isBlank()) {
                return null;
            }
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Double dblObj(HttpServletRequest r, String name) {
        try {
            String v = r.getParameter(name);
            if (v == null || v.isBlank()) {
                return null;
            }
            return Double.parseDouble(v.trim());
        } catch (Exception e) {
            return null;
        }
    }
}

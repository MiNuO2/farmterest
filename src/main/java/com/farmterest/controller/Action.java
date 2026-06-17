package com.farmterest.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 요청 처리 단위의 공통 계약.
 * FrontController 가 요청을 받아 알맞은 Action 으로 위임한다.
 */
public interface Action {
    ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
}

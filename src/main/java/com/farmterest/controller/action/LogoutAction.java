package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 로그아웃: 세션 무효화. */
public class LogoutAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        return ActionForward.redirect(request.getContextPath() + "/main.do");
    }
}

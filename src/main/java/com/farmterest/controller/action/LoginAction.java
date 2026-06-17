package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.service.AuthService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 로그인: GET=폼, POST=인증 후 세션 저장. */
public class LoginAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return ActionForward.forward("/WEB-INF/views/login.jsp");
        }

        String loginId = Params.str(request, "loginId");
        String password = Params.str(request, "password");
        MemberDTO member = new AuthService().login(loginId, password);

        if (member == null) {
            request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            request.setAttribute("loginId", loginId);
            return ActionForward.forward("/WEB-INF/views/login.jsp");
        }

        request.getSession().setAttribute(Params.LOGIN, member);
        return ActionForward.redirect(request.getContextPath() + "/main.do");
    }
}

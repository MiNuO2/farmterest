package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.service.AuthService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 회원가입: GET=폼, POST=가입 처리. */
public class JoinAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return ActionForward.forward("/WEB-INF/views/join.jsp");
        }

        MemberDTO m = new MemberDTO();
        m.setLoginId(Params.str(request, "loginId"));
        m.setPassword(Params.str(request, "password"));
        m.setName(Params.str(request, "name"));
        String role = Params.str(request, "role");
        m.setRole("SELLER".equals(role) ? "SELLER" : "CONSUMER");
        m.setRegion(Params.str(request, "region"));

        if (m.getLoginId() == null || m.getPassword() == null || m.getName() == null) {
            request.setAttribute("error", "아이디·비밀번호·이름은 필수입니다.");
            return ActionForward.forward("/WEB-INF/views/join.jsp");
        }

        boolean ok = new AuthService().join(m);
        if (!ok) {
            request.setAttribute("error", "이미 사용 중인 아이디입니다.");
            return ActionForward.forward("/WEB-INF/views/join.jsp");
        }

        return ActionForward.redirect(request.getContextPath() + "/login.do?joined=1");
    }
}

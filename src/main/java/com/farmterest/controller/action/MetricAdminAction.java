package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.MetricDefinitionDAO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 지표 관리(관리자). 테스트용이라 별도 관리자 계정/권한 없이 접근 가능.
 * GET=대기·전체 지표 목록, POST=승인/거부(승인 시 도움말 부착).
 */
public class MetricAdminAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MetricDefinitionDAO dao = new MetricDefinitionDAO();

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            int defId = Params.intOr(request, "defId", 0);
            String act = Params.str(request, "act");
            if (defId > 0 && "reject".equals(act)) {
                dao.reject(defId);
            } else if (defId > 0 && "approve".equals(act)) {
                boolean goodHigh = !"0".equals(Params.str(request, "goodHigh"));
                dao.approve(defId,
                        Params.str(request, "helpSummary"),
                        Params.str(request, "helpBody"),
                        goodHigh,
                        Params.dblObj(request, "gaugeMin"),
                        Params.dblObj(request, "gaugeMax"));
            }
            return ActionForward.redirect(request.getContextPath() + "/metricAdmin.do");
        }

        request.setAttribute("defs", dao.findAll());
        return ActionForward.forward("/WEB-INF/views/metricAdmin.jsp");
    }
}

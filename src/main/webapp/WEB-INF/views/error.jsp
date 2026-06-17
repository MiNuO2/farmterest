<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<% request.setAttribute("pageTitle", "오류 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="container">
    <div class="empty">
        <div class="ic">🌾</div>
        <h3>문제가 발생했습니다</h3>
        <p class="muted">${not empty error ? error : '요청을 처리하는 중 오류가 났습니다. 잠시 후 다시 시도해 주세요.'}</p>
        <a href="${ctx}/main.do" class="btn btn-forest" style="margin-top:18px;">홈으로 돌아가기</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

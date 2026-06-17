<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "로그인 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-wrap">
    <div class="page-head center">
        <h1>로그인</h1>
    </div>

    <div class="auth-card">
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <c:if test="${param.joined eq '1'}">
            <div class="alert alert-ok">회원가입이 완료됐어요. 로그인해 주세요.</div>
        </c:if>

        <form method="post" action="${ctx}/login.do">
            <div class="field">
                <label for="loginId">아이디</label>
                <input type="text" id="loginId" name="loginId" value="${fn:escapeXml(loginId)}" autocomplete="username">
            </div>
            <div class="field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-block">로그인</button>
        </form>

        <p class="center muted" style="margin-top:18px;">
            아직 회원이 아니신가요? <a href="${ctx}/join.do">회원가입</a>
        </p>

        <div class="alert muted" style="margin-top:8px; margin-bottom:0;">
            체험용 계정 — 소비자 <b class="mono">user1</b> · 판매자 <b class="mono">seller1</b> (비밀번호 모두 <b class="mono">1234</b>)
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

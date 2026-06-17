<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "회원가입 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-wrap">
    <div class="auth-card">
        <p class="eyebrow center">Farmterest</p>
        <h1 class="center" style="margin:4px 0 22px;">회원가입</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${ctx}/join.do">
            <div class="field">
                <label for="loginId">아이디</label>
                <input type="text" id="loginId" name="loginId" value="${fn:escapeXml(param.loginId)}" placeholder="아이디를 입력하세요" required>
            </div>

            <div class="field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <div class="field">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" value="${fn:escapeXml(param.name)}" placeholder="이름을 입력하세요" required>
            </div>

            <div class="field">
                <label>회원 유형</label>
                <div class="role-pick">
                    <label>
                        <input type="radio" name="role" value="CONSUMER" ${param.role eq 'SELLER' ? '' : 'checked'}>
                        <span>소비자</span>
                    </label>
                    <label>
                        <input type="radio" name="role" value="SELLER" ${param.role eq 'SELLER' ? 'checked' : ''}>
                        <span>판매자</span>
                    </label>
                </div>
            </div>

            <div class="field">
                <label for="region">활동 지역 <span class="muted">(선택)</span></label>
                <select id="region" name="region">
                    <option value="">선택 안 함</option>
                    <c:forEach var="r" items="${['철원','강릉','평창','정선','홍천','횡성','영월','춘천','속초','동해','고성','양구']}">
                        <option value="${r}" ${param.region eq r ? 'selected' : ''}>${r}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block">가입하기</button>
        </form>

        <p class="center muted" style="margin:18px 0 0;">
            이미 회원이신가요? <a href="${ctx}/login.do">로그인</a>
        </p>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

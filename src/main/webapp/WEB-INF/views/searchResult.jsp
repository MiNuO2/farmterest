<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "검색결과 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="section" style="padding-top:24px;">
    <div class="container">

        <!-- 페이지 머리: 빵부스러기 + 제목 -->
        <div class="page-head">
            <p class="crumb"><a href="${ctx}/main.do">홈</a> / 검색</p>
            <h1>검색결과 “${fn:escapeXml(query)}”</h1>
        </div>

        <!-- 다시 찾기: AI 맞춤검색 바 -->
        <div class="ai-search">
            <form action="${ctx}/search.do" method="get">
                <span class="tag-ai">AI 맞춤검색</span>
                <input type="text" name="q" value="${fn:escapeXml(query)}"
                       placeholder="예) 정백도 높은 평창 햅쌀 저렴하게" autocomplete="off">
                <button type="submit" class="btn btn-primary">검색</button>
            </form>
        </div>

        <!-- AI 응답 패널: LLM 설명이 있으면 우선, 없으면 규칙기반 해석 -->
        <c:choose>
            <c:when test="${aiUsed and not empty aiExplanation}">
                <div class="ai-answer">
                    <div class="ai-answer-head">
                        <span class="tag-ai">✦ AI 추천</span>
                    </div>
                    <p class="ai-answer-text">${aiExplanation}</p>
                </div>
            </c:when>
            <c:when test="${not empty criteria.understood}">
                <div class="understood">
                    <span class="lab">AI가 이렇게 이해했어요</span>
                    <span class="chip">${criteria.understood}</span>
                </div>
            </c:when>
        </c:choose>

        <c:choose>
            <%-- 검색 결과 없음 --%>
            <c:when test="${resultCount == 0}">
                <div class="empty">
                    <div class="ic">
                        <svg width="44" height="44" viewBox="0 0 24 24" fill="none"
                             stroke="currentColor" stroke-width="1.6"
                             stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <circle cx="11" cy="11" r="7"></circle>
                            <line x1="21" y1="21" x2="16.5" y2="16.5"></line>
                        </svg>
                    </div>
                    <h3>검색 결과가 없어요</h3>
                    <p>찾으시는 조건에 맞는 상품이 아직 없어요. 아래 예시로 다시 찾아보세요.</p>
                    <div class="tag-list">
                        <a class="chip" href="${ctx}/search.do?q=정백도 높은 철원 쌀">정백도 높은 철원 쌀</a>
                        <a class="chip" href="${ctx}/search.do?q=평창 감자">평창 감자</a>
                        <a class="chip" href="${ctx}/search.do?q=강릉 황태">강릉 황태</a>
                    </div>
                </div>
            </c:when>

            <%-- 로그인 회원: 취향 반영 추천 정렬 --%>
            <c:when test="${not empty recommendations}">
                <p class="muted center" style="margin:18px 0 22px;">회원님 취향을 반영해 정렬했어요</p>
                <div class="product-grid">
                    <c:forEach var="rec" items="${recommendations}">
                        <ui:productCard product="${rec.product}" ctx="${ctx}"
                                        reason="${rec.reason}" score="${rec.scorePercent}" />
                    </c:forEach>
                </div>
            </c:when>

            <%-- 기본: 검색 결과 목록 --%>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="p" items="${products}">
                        <ui:productCard product="${p}" ctx="${ctx}" />
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

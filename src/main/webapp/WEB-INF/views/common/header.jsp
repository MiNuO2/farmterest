<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- 실시간 인기 검색어(소비자 검색 로그 집계, 짧은 캐시로 새로고침마다 갱신) --%>
<% request.setAttribute("trendingKeywords", com.farmterest.service.TrendingKeywords.top(8)); %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? '팜터레스트 — 강원 산지 직거래' : pageTitle}</title>
    <meta name="description" content="강원도 농수산물 직거래. 정백도·완전립·식미치까지 품질을 숫자로 공개하고, 취향에 맞춰 추천합니다.">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Gowun+Batang:wght@400;700&family=Spline+Sans+Mono:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css?v=20260621g">
</head>
<body>

<div class="topbar">
    <div class="container">
        <span><span class="tb-mark">●</span> 강원 산지에서 오늘 출발 · 품질지표 전 상품 공개</span>
        <span>고객센터 1588-0000</span>
    </div>
</div>

<header class="site-header">
    <div class="container nav">
        <a href="${ctx}/main.do" class="brand">
            <span class="leaf"></span>팜터레스트<small>FARMTEREST</small>
        </a>

        <form class="nav-search" action="${ctx}/search.do" method="get">
            <input type="text" name="q" autocomplete="off"
                   placeholder="${empty trendingKeywords ? '예) 정백도 높은 평창 햅쌀' : ''}"
                   value="${fn:escapeXml(param.q)}">
            <c:if test="${not empty trendingKeywords}">
                <div class="ns-ticker" aria-hidden="true">
                    <span class="ns-ticker-label">실시간</span>
                    <div class="ns-ticker-roll">
                        <c:forEach var="kw" items="${trendingKeywords}"><span>${fn:escapeXml(kw)}</span></c:forEach>
                    </div>
                </div>
            </c:if>
            <button type="submit" aria-label="검색">⌕</button>
        </form>

        <button class="nav-toggle" aria-label="메뉴" onclick="document.getElementById('navActions').classList.toggle('mobile-open')">☰</button>

        <nav class="nav-actions" id="navActions">
            <c:choose>
                <%-- 판매자 전용 메뉴 (소비자와 완전히 다른 구성) --%>
                <c:when test="${not empty sessionScope.loginMember and sessionScope.loginMember.seller}">
                    <a href="${ctx}/mypage.do" class="lbl">판매 대시보드</a>
                    <a href="${ctx}/sellerProducts.do" class="lbl">상품 관리</a>
                    <a href="${ctx}/productList.do" class="lbl">마켓</a>
                    <a href="${ctx}/sellerProductForm.do" class="btn btn-primary btn-sm">+ 상품 등록</a>
                    <a href="${ctx}/mypage.do"><span class="badge badge-soft">판매자</span> <b>${sessionScope.loginMember.name}</b>님</a>
                    <a href="${ctx}/logout.do" class="lbl">로그아웃</a>
                </c:when>
                <%-- 소비자 메뉴 --%>
                <c:when test="${not empty sessionScope.loginMember}">
                    <a href="${ctx}/productList.do" class="lbl">전체상품</a>
                    <a href="${ctx}/cart.do">장바구니<c:if test="${not empty sessionScope.cart}"><span class="cart-badge">${fn:length(sessionScope.cart)}</span></c:if></a>
                    <a href="${ctx}/mypage.do"><b>${sessionScope.loginMember.name}</b>님</a>
                    <a href="${ctx}/logout.do" class="lbl">로그아웃</a>
                </c:when>
                <%-- 비로그인 --%>
                <c:otherwise>
                    <a href="${ctx}/productList.do" class="lbl">전체상품</a>
                    <a href="${ctx}/cart.do">장바구니<c:if test="${not empty sessionScope.cart}"><span class="cart-badge">${fn:length(sessionScope.cart)}</span></c:if></a>
                    <a href="${ctx}/login.do">로그인</a>
                    <a href="${ctx}/join.do" class="btn btn-primary btn-sm">회원가입</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<main>

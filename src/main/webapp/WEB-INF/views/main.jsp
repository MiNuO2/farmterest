<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "팜터레스트 — 강원의 진짜를, 숫자로 고른다"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 히어로: 자연어 AI 검색이 주인공 -->
<section class="hero center">
    <div class="container">
        <p class="eyebrow">강원도 농수산물 직거래</p>
        <h1>강원의 진짜를,<br><span class="hl">숫자로</span> 고른다.</h1>
        <p class="lead">정백도·완전립·식미치까지 공개하는 산지 직거래.
            원하는 걸 말로 검색하면, 취향에 맞춰 골라드립니다.</p>

        <div class="ai-search">
            <form action="${ctx}/search.do" method="get">
                <span class="tag-ai">AI 맞춤검색</span>
                <input type="text" name="q" placeholder="예) 정백도 높은 평창 햅쌀 저렴하게" autocomplete="off">
                <button type="submit" class="btn btn-primary">검색</button>
            </form>
            <div class="search-examples">
                <a data-example="정백도 높은 철원 쌀">정백도 높은 철원 쌀</a>
                <a data-example="평창 감자 저렴하게">평창 감자 저렴하게</a>
                <a data-example="강릉 황태">강릉 황태</a>
                <a data-example="식미치 좋은 햅쌀">식미치 좋은 햅쌀</a>
            </div>
        </div>
    </div>
</section>

<!-- 로그인 회원 맞춤 추천 -->
<c:if test="${not empty recommendations}">
    <section class="section" style="padding-top:8px;">
        <div class="container">
            <div class="section-head">
                <div>
                    <p class="eyebrow">For ${sessionScope.loginMember.name}</p>
                    <h2>회원님을 위한 맞춤 추천</h2>
                </div>
                <a href="${ctx}/mypage.do" class="more">선호 분석 보기 →</a>
            </div>
            <div class="product-grid">
                <c:forEach var="rec" items="${recommendations}">
                    <ui:productCard product="${rec.product}" ctx="${ctx}" reason="${rec.reason}" score="${rec.scorePercent}" />
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>

<!-- 인기 산지 상품 -->
<section class="section">
    <div class="container">
        <div class="section-head">
            <div>
                <p class="eyebrow">Best from Gangwon</p>
                <h2>지금 인기 산지 상품</h2>
            </div>
            <a href="${ctx}/productList.do" class="more">전체 보기 →</a>
        </div>
        <div class="product-grid">
            <c:forEach var="p" items="${popularProducts}">
                <ui:productCard product="${p}" ctx="${ctx}" />
            </c:forEach>
        </div>
    </div>
</section>

<!-- 가치 제안 -->
<section class="section" style="background:var(--paper-2); border-top:1px solid var(--line); border-bottom:1px solid var(--line);">
    <div class="container">
        <div class="section-head"><div>
            <p class="eyebrow">Why Farmterest</p>
            <h2>왜 팜터레스트인가</h2>
        </div></div>
        <div class="product-grid cols-3">
            <div class="panel">
                <h3>🔬 품질을 숫자로</h3>
                <p class="muted">정백도·완전립 비율·수분·식미치를 모든 상품에 공개합니다. 더 이상 ‘좋아요’만 믿지 마세요.</p>
            </div>
            <div class="panel">
                <h3>✦ 취향까지 읽는 검색</h3>
                <p class="muted">“정백도 높은 평창 쌀”처럼 말로 검색하면, 구매이력을 반영해 <b>이유와 함께</b> 추천합니다.</p>
            </div>
            <div class="panel">
                <h3>🚚 산지에서 바로</h3>
                <p class="muted">강원 생산자와 직접 연결. 중간 단계를 줄여 신선하고 합리적으로.</p>
            </div>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", ((String) request.getAttribute("category")) + " — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div class="container">
        <div class="crumb">
            <a href="${ctx}/main.do">홈</a> /
            <a href="${ctx}/productList.do">전체상품</a> /
            ${category}
        </div>
        <h1>${category}
            <c:if test="${seasonal}"><span class="badge badge-season">제철</span></c:if>
        </h1>
        <p class="muted">${category} 품목의 이달의 인기 상품과 전체 상품을 모았어요.</p>
    </div>
</section>

<!-- 이달의 인기(최고) 상품: 별점 + 판매량 기반 선정 -->
<c:if test="${not empty best}">
    <c:set var="bp" value="${best.product}" />
    <section class="section" style="padding-top:14px;">
        <div class="container">
            <div class="section-head"><div>
                <p class="eyebrow">Best of the month</p>
                <h2>이달의 인기 ${category}</h2>
            </div></div>

            <div class="best-hero">
                <a class="bh-art" href="${ctx}/productDetail.do?id=${bp.productId}">
                    <span class="bh-ribbon">이달의 인기</span>
                    <ui:categoryArt category="${bp.category}" region="${bp.region}" big="true"
                                    imageUrl="${bp.imageUrl}" ctx="${ctx}" name="${bp.name}" />
                </a>
                <div class="bh-body">
                    <div class="bh-reason">${best.reason}</div>
                    <a href="${ctx}/productDetail.do?id=${bp.productId}"><h3 class="bh-name">${bp.name}</h3></a>
                    <div class="card-meta">
                        <span>${bp.sellerName}</span><span class="dot"></span><span>강원 ${bp.region}</span>
                    </div>
                    <c:if test="${bp.reviewCount > 0}">
                        <ui:stars rating="${bp.avgRating}" count="${bp.reviewCount}" />
                    </c:if>
                    <c:if test="${bp.hasQuality()}">
                        <div class="quality-badges">
                            <c:if test="${not empty bp.polishedRate}"><span class="badge">정백도 <b>${bp.polishedRate}</b>%</span></c:if>
                            <c:if test="${not empty bp.tasteScore}"><span class="badge">식미치 <b>${bp.tasteScore}</b></span></c:if>
                            <c:if test="${not empty bp.wholeGrainRate}"><span class="badge badge-soft">완전립 <b>${bp.wholeGrainRate}</b>%</span></c:if>
                        </div>
                    </c:if>
                    <p class="bh-desc">${bp.description}</p>
                    <div class="bh-foot">
                        <span class="price"><fmt:formatNumber value="${bp.price}" type="number"/><small> 원</small></span>
                        <span class="bh-actions">
                            <a href="${ctx}/productDetail.do?id=${bp.productId}" class="btn btn-ghost btn-sm">자세히</a>
                            <a href="${ctx}/cartAdd.do?id=${bp.productId}&qty=1" class="btn btn-primary btn-sm">담기</a>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </section>
</c:if>

<!-- 해당 품목 전체 상품 -->
<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="section-head">
            <div>
                <p class="eyebrow">All in ${category}</p>
                <h2>${category} 상품 <span class="count-inline">${resultCount}개</span></h2>
            </div>
            <a href="${ctx}/productList.do?category=${category}" class="more">상세 필터로 보기 →</a>
        </div>

        <c:choose>
            <c:when test="${resultCount eq 0}">
                <div class="empty">
                    <h3>아직 이 품목에 상품이 없어요</h3>
                    <p><a href="${ctx}/productList.do" class="btn btn-forest btn-sm">전체 상품 보기</a></p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="p" items="${products}">
                        <c:if test="${empty best or p.productId ne best.product.productId}">
                            <ui:productCard product="${p}" ctx="${ctx}" />
                        </c:if>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

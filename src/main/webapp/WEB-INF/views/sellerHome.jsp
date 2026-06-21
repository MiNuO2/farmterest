<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "판매 대시보드 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 인사 + 판매자 전용 헤더 -->
<section class="page-head seller-head">
    <div class="container">
        <p class="crumb"><a href="${ctx}/main.do">홈</a> · 판매 대시보드</p>
        <div class="seller-head-row">
            <div>
                <h1>${sessionScope.loginMember.name}님의 판매 대시보드
                    <span class="badge badge-soft">판매자</span>
                </h1>
                <p class="muted">내 상품의 판매·매출·재고·평점을 한눈에 관리하세요.</p>
            </div>
            <div class="seller-actions">
                <a href="${ctx}/sellerProductForm.do" class="btn btn-primary">+ 상품 등록</a>
                <a href="${ctx}/sellerProducts.do" class="btn btn-ghost">상품 관리</a>
            </div>
        </div>
    </div>
</section>

<c:choose>
    <c:when test="${empty products}">
        <!-- 등록 상품이 없을 때 -->
        <section class="section" style="padding-top:8px;">
            <div class="container">
                <div class="empty">
                    <div class="ic">
                        <svg width="46" height="46" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M3 9l1-4h16l1 4"/><path d="M4 9v10a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V9"/><path d="M9 13h6"/>
                        </svg>
                    </div>
                    <h3>아직 등록한 상품이 없어요</h3>
                    <p>첫 상품을 등록하면 판매·매출·평점 현황이 이 대시보드에 채워집니다.</p>
                    <p style="margin-top:16px;"><a href="${ctx}/sellerProductForm.do" class="btn btn-primary">+ 첫 상품 등록하기</a></p>
                </div>
            </div>
        </section>
    </c:when>
    <c:otherwise>

    <!-- 요약 통계 -->
    <section class="section" style="padding-top:8px;">
        <div class="container">
            <div class="section-head"><div>
                <p class="eyebrow">Overview</p>
                <h2>판매 요약</h2>
            </div></div>
            <div class="dash-grid">
                <div class="stat-card">
                    <div class="lab">등록 상품</div>
                    <div class="num">${stats.totalProducts}<small> 개</small></div>
                    <div class="sub">총 재고 <fmt:formatNumber value="${stats.totalStock}" type="number"/>개</div>
                </div>
                <div class="stat-card">
                    <div class="lab">누적 판매량</div>
                    <div class="num"><fmt:formatNumber value="${stats.totalSold}" type="number"/><small> 개</small></div>
                    <div class="sub">이달 <fmt:formatNumber value="${stats.monthSold}" type="number"/>개 판매</div>
                </div>
                <div class="stat-card">
                    <div class="lab">누적 매출</div>
                    <div class="num"><fmt:formatNumber value="${stats.totalRevenue}" type="number"/><small> 원</small></div>
                    <div class="sub">판매가 합계 기준</div>
                </div>
                <div class="stat-card">
                    <div class="lab">평균 평점</div>
                    <div class="num">
                        <c:choose>
                            <c:when test="${stats.reviewCount > 0}">${stats.avgRating}<small> / 5</small></c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="sub">
                        <c:choose>
                            <c:when test="${stats.reviewCount > 0}"><ui:stars stars="${stats.ratingStars}" size="sm" /> 후기 ${stats.reviewCount}건</c:when>
                            <c:otherwise>아직 후기 없음</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 재고 부족 알림 -->
    <c:if test="${stats.lowStockCount > 0}">
        <section class="section" style="padding-top:0;">
            <div class="container">
                <div class="panel panel-warn">
                    <h3>재고 부족 알림 <span class="warn-count">${stats.lowStockCount}</span></h3>
                    <p class="muted" style="margin:2px 0 12px;">재고가 10개 이하인 상품이에요. 보충을 고려해 보세요.</p>
                    <div class="low-list">
                        <c:forEach var="p" items="${products}">
                            <c:if test="${p.lowStock}">
                                <div class="low-item">
                                    <a href="${ctx}/productDetail.do?id=${p.productId}">${p.name}</a>
                                    <span class="low-stock">재고 ${p.stock}개</span>
                                    <a href="${ctx}/sellerProductForm.do?id=${p.productId}" class="btn btn-ghost btn-sm">재고 수정</a>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </section>
    </c:if>

    <!-- 베스트셀러 -->
    <c:if test="${not empty stats.bestSeller}">
        <c:set var="bp" value="${stats.bestSeller}" />
        <section class="section" style="padding-top:0;">
            <div class="container">
                <div class="best-seller">
                    <span class="bs-badge">베스트셀러</span>
                    <a class="bs-art" href="${ctx}/productDetail.do?id=${bp.productId}">
                        <ui:categoryArt category="${bp.category}" region="${bp.region}"
                                        imageUrl="${bp.imageUrl}" ctx="${ctx}" name="${bp.name}" />
                    </a>
                    <div class="bs-body">
                        <a href="${ctx}/productDetail.do?id=${bp.productId}"><h3>${bp.name}</h3></a>
                        <div class="card-meta"><span>${bp.category}</span><span class="dot"></span><span>강원 ${bp.region}</span></div>
                        <p class="bs-stat">
                            누적 <b><fmt:formatNumber value="${bp.totalSold}" type="number"/>개</b> 판매 ·
                            매출 <b><fmt:formatNumber value="${bp.revenue}" type="number"/>원</b>
                            <c:if test="${bp.reviewCount > 0}"> · <ui:stars rating="${bp.avgRating}" count="${bp.reviewCount}" size="sm" /></c:if>
                        </p>
                    </div>
                </div>
            </div>
        </section>
    </c:if>

    <!-- 상품별 판매 현황 -->
    <section class="section" style="padding-top:0;">
        <div class="container">
            <div class="section-head"><div>
                <p class="eyebrow">My Products</p>
                <h2>상품별 현황 <span class="count-inline">${stats.totalProducts}개</span></h2>
            </div>
                <a href="${ctx}/sellerProducts.do" class="more">상품 관리 →</a>
            </div>
            <div class="table-wrap">
                <table class="table">
                    <thead>
                        <tr>
                            <th>상품명</th>
                            <th>품목</th>
                            <th class="num">가격</th>
                            <th class="num">재고</th>
                            <th class="num">누적 판매</th>
                            <th class="num">이달 판매</th>
                            <th class="num">매출</th>
                            <th>평점</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td><a href="${ctx}/productDetail.do?id=${p.productId}">${p.name}</a></td>
                                <td>${p.category}</td>
                                <td class="num"><fmt:formatNumber value="${p.price}" type="number"/></td>
                                <td class="num">
                                    <c:choose>
                                        <c:when test="${p.lowStock}"><span class="stock-low">${p.stock}</span></c:when>
                                        <c:otherwise>${p.stock}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="num"><fmt:formatNumber value="${p.totalSold}" type="number"/></td>
                                <td class="num"><fmt:formatNumber value="${p.monthSold}" type="number"/></td>
                                <td class="num"><fmt:formatNumber value="${p.revenue}" type="number"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.reviewCount > 0}"><ui:stars rating="${p.avgRating}" count="${p.reviewCount}" size="sm" /></c:when>
                                        <c:otherwise><span class="muted">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${ctx}/sellerProductForm.do?id=${p.productId}" class="btn btn-ghost btn-sm">수정</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <!-- 최근 받은 후기 -->
    <section class="section" style="padding-top:0;">
        <div class="container">
            <div class="section-head"><div>
                <p class="eyebrow">Recent Reviews</p>
                <h2>최근 받은 후기</h2>
            </div></div>
            <c:choose>
                <c:when test="${not empty recentReviews}">
                    <div class="review-list">
                        <c:forEach var="rv" items="${recentReviews}">
                            <div class="review-item">
                                <div class="rv-top">
                                    <ui:stars stars="${rv.rating}" size="sm" />
                                    <a class="rv-prod" href="${ctx}/productDetail.do?id=${rv.productId}">${rv.productName}</a>
                                    <span class="rv-who">${fn:escapeXml(rv.memberName)}</span>
                                    <span class="rv-date"><fmt:formatDate value="${rv.createdAt}" pattern="yyyy.MM.dd"/></span>
                                </div>
                                <c:if test="${not empty rv.comment}"><p class="rv-cmt">${fn:escapeXml(rv.comment)}</p></c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="panel"><p class="muted">아직 받은 후기가 없어요. 구매 고객이 별점을 남기면 여기에 모여요.</p></div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "마이페이지 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 인사 + 역할 배지 -->
<section class="page-head">
    <div class="container">
        <p class="crumb"><a href="${ctx}/main.do">홈</a> · 마이페이지</p>
        <h1>${sessionScope.loginMember.name}님, 안녕하세요
            <c:choose>
                <c:when test="${sessionScope.loginMember.seller}"><span class="badge badge-soft">판매자</span></c:when>
                <c:otherwise><span class="badge">소비자</span></c:otherwise>
            </c:choose>
        </h1>
        <p class="muted">구매 이력으로 알아본 내 취향과 주문 내역을 한눈에 보여드려요.</p>
    </div>
</section>

<!-- 선호도 대시보드 -->
<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="section-head"><div>
            <p class="eyebrow">My Taste</p>
            <h2>나의 취향 분석</h2>
        </div></div>

        <div class="dash-grid">
            <div class="stat-card">
                <div class="lab">선호 품목</div>
                <div class="num">${not empty profile.topCategory ? profile.topCategory : '-'}</div>
                <div class="sub">가장 자주 산 품목</div>
            </div>
            <div class="stat-card">
                <div class="lab">선호 지역</div>
                <div class="num">${not empty profile.topRegion ? profile.topRegion : '-'}</div>
                <div class="sub">가장 자주 산 산지</div>
            </div>
            <div class="stat-card">
                <div class="lab">선호 정백도</div>
                <div class="num">
                    <c:choose>
                        <c:when test="${profile.avgPolishedRate > 0}">${profile.avgPolishedRate}%</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </div>
                <div class="sub">구매한 쌀 평균</div>
            </div>
            <div class="stat-card">
                <div class="lab">구매 상품수</div>
                <div class="num">${profile.purchaseCount}</div>
                <div class="sub">지금까지 산 상품 종류</div>
            </div>
        </div>

        <c:if test="${profile.isEmpty()}">
            <p class="muted" style="margin-top:14px;">아직 구매 이력이 없어요. 첫 주문을 하시면 취향에 맞춰 골라드릴게요.
                <a href="${ctx}/productList.do" style="color:var(--clay);font-weight:600;">상품 보러 가기 →</a></p>
        </c:if>
    </div>
</section>

<!-- 최근 검색어 -->
<section class="section" style="padding-top:0;">
    <div class="container">
        <div class="panel">
            <h3>최근 검색어</h3>
            <c:choose>
                <c:when test="${not empty recentSearches}">
                    <div class="tag-list">
                        <c:forEach var="kw" items="${recentSearches}">
                            <c:url var="kwUrl" value="/search.do"><c:param name="q" value="${kw}"/></c:url>
                            <a class="chip" href="${kwUrl}">${fn:escapeXml(kw)}</a>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="muted">아직 검색 기록이 없어요. 말로 검색하면 취향에 맞춰 찾아드려요.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<!-- 맞춤 추천 -->
<c:if test="${not empty recommendations}">
    <section class="section" style="padding-top:0;">
        <div class="container">
            <div class="section-head"><div>
                <p class="eyebrow">For ${sessionScope.loginMember.name}</p>
                <h2>회원님 맞춤 추천</h2>
            </div>
                <a href="${ctx}/productList.do" class="more">더 보기 →</a>
            </div>
            <div class="product-grid">
                <c:forEach var="rec" items="${recommendations}">
                    <ui:productCard product="${rec.product}" ctx="${ctx}" reason="${rec.reason}" score="${rec.scorePercent}" />
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>

<!-- 주문 내역 -->
<section class="section" style="padding-top:0;">
    <div class="container">
        <div class="section-head"><div>
            <p class="eyebrow">My Orders</p>
            <h2>주문 내역</h2>
        </div></div>

        <c:choose>
            <c:when test="${not empty orders}">
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <div class="oc-head">
                            <span>주문번호 <b class="mono">#${order.orderId}</b>
                                <span class="muted"> · <fmt:formatDate value="${order.orderedAt}" pattern="yyyy.MM.dd"/></span>
                            </span>
                            <c:choose>
                                <c:when test="${order.status == 'PAID'}"><span class="status status-PAID">결제완료</span></c:when>
                                <c:when test="${order.status == 'SHIPPING'}"><span class="status status-SHIPPING">배송중</span></c:when>
                                <c:when test="${order.status == 'DONE'}"><span class="status status-DONE">완료</span></c:when>
                                <c:when test="${order.status == 'CANCEL'}"><span class="status status-CANCEL">취소</span></c:when>
                                <c:otherwise><span class="status">${order.status}</span></c:otherwise>
                            </c:choose>
                        </div>
                        <div class="oc-body">
                            <c:forEach var="item" items="${order.items}">
                                <div class="oc-line">
                                    <span><a href="${ctx}/productDetail.do?id=${item.productId}" class="oc-prod">${item.productName}</a> x${item.qty}</span>
                                    <span class="mono"><fmt:formatNumber value="${item.subtotal}" type="number"/> 원</span>
                                </div>
                                <c:set var="rv" value="${myReviews[item.orderItemId]}" />
                                <div class="oc-review">
                                    <c:choose>
                                        <c:when test="${not empty rv}">
                                            <div class="my-review">
                                                <span class="mr-label">내 평가</span>
                                                <ui:stars stars="${rv.rating}" size="sm" />
                                                <c:if test="${not empty rv.comment}"><span class="mr-cmt">${fn:escapeXml(rv.comment)}</span></c:if>
                                                <details class="mr-edit">
                                                    <summary>수정</summary>
                                                    <form class="review-form" action="${ctx}/reviewSave.do" method="post">
                                                        <input type="hidden" name="orderItemId" value="${item.orderItemId}">
                                                        <div class="star-input">
                                                            <c:forEach var="s" items="${[5,4,3,2,1]}">
                                                                <input type="radio" id="r${item.orderItemId}-${s}" name="rating" value="${s}" <c:if test="${rv.rating eq s}">checked</c:if>>
                                                                <label for="r${item.orderItemId}-${s}" title="${s}점">★</label>
                                                            </c:forEach>
                                                        </div>
                                                        <input type="text" name="comment" maxlength="500" value="${fn:escapeXml(rv.comment)}" placeholder="후기를 남겨주세요 (선택)">
                                                        <button type="submit" class="btn btn-forest btn-sm">수정</button>
                                                    </form>
                                                </details>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <form class="review-form" action="${ctx}/reviewSave.do" method="post">
                                                <input type="hidden" name="orderItemId" value="${item.orderItemId}">
                                                <span class="rf-q">이 상품 어떠셨나요?</span>
                                                <div class="star-input">
                                                    <c:forEach var="s" items="${[5,4,3,2,1]}">
                                                        <input type="radio" id="r${item.orderItemId}-${s}" name="rating" value="${s}">
                                                        <label for="r${item.orderItemId}-${s}" title="${s}점">★</label>
                                                    </c:forEach>
                                                </div>
                                                <input type="text" name="comment" maxlength="500" placeholder="후기를 남겨주세요 (선택)">
                                                <button type="submit" class="btn btn-forest btn-sm">별점 등록</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                            <div class="oc-line" style="font-weight:700;">
                                <span>합계</span>
                                <span class="price"><fmt:formatNumber value="${order.totalPrice}" type="number"/><small> 원</small></span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty">
                    <div class="ic">
                        <svg width="46" height="46" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
                            <path d="M3 6h18"/><path d="M16 10a4 4 0 0 1-8 0"/>
                        </svg>
                    </div>
                    <h3>아직 주문 내역이 없어요</h3>
                    <p>마음에 드는 강원 산지 상품을 담아보세요.</p>
                    <a href="${ctx}/productList.do" class="btn btn-primary" style="margin-top:14px;">상품 보러 가기</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "장바구니 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-head">
    <div class="container">
        <div class="crumb"><a href="${ctx}/main.do">홈</a> · 장바구니</div>
        <h1>장바구니</h1>
    </div>
</div>

<section class="section" style="padding-top:0;">
    <div class="container">

        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="empty">
                    <div class="ic">
                        <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <circle cx="9" cy="20" r="1"></circle>
                            <circle cx="18" cy="20" r="1"></circle>
                            <path d="M2 3h2l2.4 12.3a2 2 0 0 0 2 1.7h7.7a2 2 0 0 0 2-1.6L21 7H5.4"></path>
                        </svg>
                    </div>
                    <h3>장바구니가 비었어요</h3>
                    <p>강원 산지의 좋은 상품을 담아보세요.</p>
                    <p style="margin-top:18px;">
                        <a href="${ctx}/productList.do" class="btn btn-primary">상품 보러 가기</a>
                    </p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="cart-layout">

                    <!-- 왼쪽: 담은 상품 목록 -->
                    <div class="table-wrap">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>상품</th>
                                    <th class="num">단가</th>
                                    <th class="num">수량</th>
                                    <th class="num">합계</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cartItems}">
                                    <tr>
                                        <td>
                                            <a href="${ctx}/productDetail.do?id=${item.productId}">${item.productName}</a>
                                            <div class="muted" style="font-size:.78rem; margin-top:2px;">${item.category}</div>
                                        </td>
                                        <td class="num"><fmt:formatNumber value="${item.price}" type="number"/> 원</td>
                                        <td class="num">${item.qty}</td>
                                        <td class="num"><fmt:formatNumber value="${item.subtotal}" type="number"/> 원</td>
                                        <td class="num">
                                            <a href="${ctx}/cartRemove.do?id=${item.productId}" class="btn btn-ghost btn-sm">삭제</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- 오른쪽: 주문 요약 -->
                    <aside class="summary">
                        <h3>주문 요약</h3>
                        <div class="summary-row">
                            <span>상품 금액</span>
                            <span class="mono"><fmt:formatNumber value="${cartTotal}" type="number"/> 원</span>
                        </div>
                        <div class="summary-row">
                            <span>배송비</span>
                            <span class="mono">무료</span>
                        </div>
                        <div class="summary-total">
                            <span>합계</span>
                            <span class="price"><fmt:formatNumber value="${cartTotal}" type="number"/><small> 원</small></span>
                        </div>

                        <a href="${ctx}/order.do" class="btn btn-primary btn-block" style="margin-top:16px;">주문하기</a>

                        <c:if test="${empty sessionScope.loginMember}">
                            <p class="muted center" style="font-size:.8rem; margin-top:12px;">주문하려면 로그인이 필요해요.</p>
                        </c:if>
                    </aside>

                </div>
            </c:otherwise>
        </c:choose>

    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

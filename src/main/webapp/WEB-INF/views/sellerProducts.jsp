<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "판매자 센터 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="crumb"><a href="${ctx}/main.do">홈</a> / 판매자 센터</p>
        <h1>판매자 센터</h1>
        <p class="muted">내가 등록한 상품을 한눈에 보고, 쉽게 고치거나 지울 수 있어요.</p>
    </div>
</section>

<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="section-head">
            <div>
                <p class="eyebrow">My Products</p>
                <h2>내 상품 목록</h2>
            </div>
            <div class="more">
                <a href="${ctx}/sellerProductForm.do" class="btn btn-primary">+ 새 상품 등록</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty products}">
                <div class="empty">
                    <div class="ic">
                        <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M3 9l1-4h16l1 4"/>
                            <path d="M4 9v10a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V9"/>
                            <path d="M9 13h6"/>
                        </svg>
                    </div>
                    <h3>아직 등록한 상품이 없어요</h3>
                    <p>첫 상품을 올리면 여기에 보여요. 지금 바로 시작해 보세요.</p>
                    <p style="margin-top:16px;">
                        <a href="${ctx}/sellerProductForm.do" class="btn btn-primary">+ 새 상품 등록</a>
                    </p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>상품명</th>
                                <th>품목</th>
                                <th>지역</th>
                                <th class="num">가격</th>
                                <th class="num">재고</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td><a href="${ctx}/productDetail.do?id=${p.productId}">${p.name}</a></td>
                                    <td>${p.category}</td>
                                    <td>${p.region}</td>
                                    <td class="num"><fmt:formatNumber value="${p.price}" type="number"/> 원</td>
                                    <td class="num"><fmt:formatNumber value="${p.stock}" type="number"/></td>
                                    <td>
                                        <a href="${ctx}/sellerProductForm.do?id=${p.productId}" class="btn btn-ghost btn-sm">수정</a>
                                        <a href="${ctx}/sellerProductDelete.do?id=${p.productId}" class="btn btn-ghost btn-sm"
                                           onclick="return confirm('정말 삭제할까요?');">삭제</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "전체 상품 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div class="container">
        <div class="crumb"><a href="${ctx}/main.do">홈</a> / 전체상품</div>
        <h1>전체 상품</h1>
    </div>
</section>

<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="shop-layout">

            <!-- 사이드바: 필터 (한 폼으로 한 번에 적용) -->
            <aside class="filter">
                <div class="filter-title">상세 필터</div>
                <form method="get" action="${ctx}/productList.do">

                    <div class="filter-group">
                        <div class="filter-head">품목</div>
                        <div class="filter-body">
                            <label>
                                <input type="radio" name="category" value=""
                                    <c:if test="${empty criteria.category}">checked</c:if>>
                                전체
                            </label>
                            <c:forEach var="cat" items="${['쌀','잡곡','감자','채소','수산']}">
                                <label>
                                    <input type="radio" name="category" value="${cat}"
                                        <c:if test="${criteria.category eq cat}">checked</c:if>>
                                    ${cat}
                                </label>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-head">지역</div>
                        <div class="filter-body">
                            <select name="region">
                                <option value="">전체</option>
                                <c:forEach var="reg" items="${['철원','강릉','평창','정선','홍천','횡성','영월','춘천','속초','동해','고성','양구']}">
                                    <option value="${reg}" <c:if test="${criteria.region eq reg}">selected</c:if>>${reg}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-head">가격</div>
                        <div class="filter-body">
                            <div class="price-range">
                                <input type="number" name="priceMin" min="0" step="1000" placeholder="최소"
                                    value="${criteria.priceMin}">
                                <span class="muted">~</span>
                                <input type="number" name="priceMax" min="0" step="1000" placeholder="최대"
                                    value="${criteria.priceMax}">
                            </div>
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-head">정백도</div>
                        <div class="filter-body">
                            <select name="minPolishedRate">
                                <option value="">전체</option>
                                <option value="80" <c:if test="${criteria.minPolishedRate eq 80}">selected</c:if>>80% 이상</option>
                                <option value="90" <c:if test="${criteria.minPolishedRate eq 90}">selected</c:if>>90% 이상</option>
                            </select>
                        </div>
                    </div>

                    <div class="filter-group">
                        <div class="filter-head">정렬</div>
                        <div class="filter-body">
                            <select name="sort">
                                <option value="relevance" <c:if test="${criteria.sort eq 'relevance'}">selected</c:if>>추천순</option>
                                <option value="price_asc" <c:if test="${criteria.sort eq 'price_asc'}">selected</c:if>>낮은가격순</option>
                                <option value="price_desc" <c:if test="${criteria.sort eq 'price_desc'}">selected</c:if>>높은가격순</option>
                                <option value="quality" <c:if test="${criteria.sort eq 'quality'}">selected</c:if>>품질좋은순</option>
                                <option value="newest" <c:if test="${criteria.sort eq 'newest'}">selected</c:if>>신상품순</option>
                            </select>
                        </div>
                    </div>

                    <div class="filter-actions">
                        <button type="submit" class="btn btn-forest btn-block">필터 적용</button>
                        <a href="${ctx}/productList.do" class="btn btn-ghost btn-block">초기화</a>
                    </div>
                </form>
            </aside>

            <!-- 결과 -->
            <div>
                <div class="result-head">
                    <div class="count">총 <b>${resultCount}</b>개</div>
                </div>

                <c:choose>
                    <c:when test="${resultCount eq 0}">
                        <div class="empty">
                            <div class="ic" aria-hidden="true">
                                <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="11" cy="11" r="7"></circle>
                                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                                </svg>
                            </div>
                            <h3>조건에 맞는 상품이 없어요</h3>
                            <p>필터를 조금 넓혀 보거나, 전체 상품을 다시 둘러보세요.</p>
                            <p><a href="${ctx}/productList.do" class="btn btn-forest btn-sm">전체 상품 보기</a></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="product-grid">
                            <c:forEach var="p" items="${products}">
                                <ui:productCard product="${p}" ctx="${ctx}" />
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="category" required="true" %>
<%@ attribute name="region" required="false" %>
<%@ attribute name="big" required="false" %>
<%@ attribute name="imageUrl" required="false" %>
<%@ attribute name="ctx" required="false" %>
<%@ attribute name="name" required="false" %>
<%--
  상품 비주얼: 이미지(로컬 합성 홈쇼핑 SVG)가 있으면 사진처럼 표시,
  없으면 품목별 라인 SVG 아이콘 + 원산지 스탬프로 폴백.
  big="true" 면 상세페이지용 큰 비주얼.
--%>
<div class="card-media cat-${category} ${big ? 'lg' : ''}">
    <c:choose>
        <c:when test="${not empty imageUrl}">
            <img class="card-photo" src="${ctx}/${imageUrl}" alt="${empty name ? category : name}" loading="lazy"
                 onerror="this.style.display='none'" />
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${category == '쌀'}">
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M24 44V16"/>
                        <path d="M24 16c0-4 3-7 8-7-1 4-3 7-8 7zM24 16c0-4-3-7-8-7 1 4 3 7 8 7z"/>
                        <path d="M24 25c0-4 3-7 8-7-1 4-3 7-8 7zM24 25c0-4-3-7-8-7 1 4 3 7 8 7z"/>
                        <path d="M24 34c0-4 3-7 8-7-1 4-3 7-8 7zM24 34c0-4-3-7-8-7 1 4 3 7 8 7z"/>
                    </svg>
                </c:when>
                <c:when test="${category == '잡곡'}">
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 23a15 15 0 0 0 30 0Z"/>
                        <circle cx="20" cy="16" r="2.4"/><circle cx="28" cy="16" r="2.4"/><circle cx="24" cy="11" r="2.4"/>
                    </svg>
                </c:when>
                <c:when test="${category == '감자'}">
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <ellipse cx="24" cy="24" rx="15" ry="11" transform="rotate(-18 24 24)"/>
                        <circle cx="19" cy="22" r="1.3" fill="currentColor" stroke="none"/>
                        <circle cx="27" cy="19" r="1.3" fill="currentColor" stroke="none"/>
                        <circle cx="26" cy="28" r="1.3" fill="currentColor" stroke="none"/>
                    </svg>
                </c:when>
                <c:when test="${category == '채소'}">
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M11 37C11 19 30 12 39 12 39 31 24 37 11 37Z"/>
                        <path d="M11 37C20 31 31 22 37 15"/>
                    </svg>
                </c:when>
                <c:when test="${category == '수산'}">
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M6 24C14 13 29 13 37 24 29 35 14 35 6 24Z"/>
                        <path d="M37 24l6-5v10z"/>
                        <circle cx="14" cy="22" r="1.6" fill="currentColor" stroke="none"/>
                    </svg>
                </c:when>
                <c:otherwise>
                    <svg class="cat-ic" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M10 18h28l-3 22H13z"/><path d="M16 18a8 8 0 0 1 16 0"/>
                    </svg>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty region}"><span class="origin-stamp">강원·${region}</span></c:if>
            <span class="cat-label">${category}</span>
        </c:otherwise>
    </c:choose>
</div>

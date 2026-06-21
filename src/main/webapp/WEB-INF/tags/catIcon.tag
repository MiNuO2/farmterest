<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="category" required="true" %>
<%-- 품목별 라인 아이콘만 렌더(버튼/칩용). 크기는 CSS(.cc-ic svg)에서 지정. --%>
<c:choose>
    <c:when test="${category == '쌀'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M24 44V16"/>
            <path d="M24 16c0-4 3-7 8-7-1 4-3 7-8 7zM24 16c0-4-3-7-8-7 1 4 3 7 8 7z"/>
            <path d="M24 25c0-4 3-7 8-7-1 4-3 7-8 7zM24 25c0-4-3-7-8-7 1 4 3 7 8 7z"/>
            <path d="M24 34c0-4 3-7 8-7-1 4-3 7-8 7zM24 34c0-4-3-7-8-7 1 4 3 7 8 7z"/>
        </svg>
    </c:when>
    <c:when test="${category == '잡곡'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 23a15 15 0 0 0 30 0Z"/>
            <circle cx="20" cy="16" r="2.4"/><circle cx="28" cy="16" r="2.4"/><circle cx="24" cy="11" r="2.4"/>
        </svg>
    </c:when>
    <c:when test="${category == '감자'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <ellipse cx="24" cy="24" rx="15" ry="11" transform="rotate(-18 24 24)"/>
            <circle cx="19" cy="22" r="1.3" fill="currentColor" stroke="none"/>
            <circle cx="27" cy="19" r="1.3" fill="currentColor" stroke="none"/>
            <circle cx="26" cy="28" r="1.3" fill="currentColor" stroke="none"/>
        </svg>
    </c:when>
    <c:when test="${category == '채소'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M11 37C11 19 30 12 39 12 39 31 24 37 11 37Z"/>
            <path d="M11 37C20 31 31 22 37 15"/>
        </svg>
    </c:when>
    <c:when test="${category == '수산'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 24C14 13 29 13 37 24 29 35 14 35 6 24Z"/>
            <path d="M37 24l6-5v10z"/>
            <circle cx="14" cy="22" r="1.6" fill="currentColor" stroke="none"/>
        </svg>
    </c:when>
    <c:when test="${category == '전체'}">
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="9" y="9" width="12" height="12" rx="2.5"/>
            <rect x="27" y="9" width="12" height="12" rx="2.5"/>
            <rect x="9" y="27" width="12" height="12" rx="2.5"/>
            <rect x="27" y="27" width="12" height="12" rx="2.5"/>
        </svg>
    </c:when>
    <c:otherwise>
        <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M10 18h28l-3 22H13z"/><path d="M16 18a8 8 0 0 1 16 0"/>
        </svg>
    </c:otherwise>
</c:choose>

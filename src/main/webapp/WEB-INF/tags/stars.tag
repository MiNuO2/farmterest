<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="rating" required="false" type="java.lang.Double" %>
<%@ attribute name="stars" required="false" type="java.lang.Integer" %>
<%@ attribute name="count" required="false" type="java.lang.Integer" %>
<%@ attribute name="size" required="false" %>
<%--
  별점 표시 위젯.
  - rating(평균, 소수 가능)이 있으면 그 비율로 채움(반쪽 별 표현 가능)
  - 없으면 stars(정수 0~5)로 채움 (개별 후기의 정확한 별점 등)
  - count 가 주어지면 숫자 라벨/후기수 표시
--%>
<c:set var="pct" value="${rating != null ? (rating / 5 * 100) : (stars != null ? stars * 20 : 0)}" />
<span class="stars ${size}">
    <span class="stars-graphic" aria-hidden="true">
        <span class="stars-track">★★★★★</span>
        <span class="stars-fill" style="width:${pct}%">★★★★★</span>
    </span>
    <c:if test="${count != null}">
        <c:choose>
            <c:when test="${count > 0}">
                <span class="stars-meta"><b><fmt:formatNumber value="${rating}" minFractionDigits="1" maxFractionDigits="1"/></b> (${count})</span>
            </c:when>
            <c:otherwise>
                <span class="stars-meta muted">평가 없음</span>
            </c:otherwise>
        </c:choose>
    </c:if>
</span>

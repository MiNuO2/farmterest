<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ attribute name="product" required="true" type="com.farmterest.model.dto.ProductDTO" %>
<%@ attribute name="ctx" required="true" %>
<%@ attribute name="reason" required="false" %>
<%@ attribute name="score" required="false" %>

<article class="card">
    <a href="${ctx}/productDetail.do?id=${product.productId}">
        <ui:categoryArt category="${product.category}" region="${product.region}"
                        imageUrl="${product.imageUrl}" ctx="${ctx}" name="${product.name}" />
    </a>
    <div class="card-body">
        <c:if test="${not empty reason}">
            <div class="rec-strip">
                <span class="rec-reason">${reason}</span>
                <c:if test="${not empty score}"><span class="rec-score">${score}점</span></c:if>
            </div>
        </c:if>
        <a href="${ctx}/productDetail.do?id=${product.productId}"><div class="card-title">${product.name}</div></a>
        <div class="card-meta">
            <span>${product.sellerName}</span><span class="dot"></span><span>${product.region}</span>
        </div>
        <c:if test="${product.hasQuality()}">
            <div class="quality-badges">
                <c:if test="${not empty product.polishedRate}"><span class="badge">정백도 <b>${product.polishedRate}</b>%</span></c:if>
                <c:if test="${not empty product.tasteScore}"><span class="badge">식미치 <b>${product.tasteScore}</b></span></c:if>
                <c:if test="${not empty product.wholeGrainRate}"><span class="badge badge-soft">완전립 <b>${product.wholeGrainRate}</b>%</span></c:if>
            </div>
        </c:if>
        <div class="card-foot">
            <span class="price"><fmt:formatNumber value="${product.price}" type="number"/><small> 원</small></span>
            <a href="${ctx}/cartAdd.do?id=${product.productId}&qty=1" class="btn btn-primary btn-sm">담기</a>
        </div>
    </div>
</article>

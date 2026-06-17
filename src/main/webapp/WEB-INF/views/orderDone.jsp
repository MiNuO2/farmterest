<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "주문 완료 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="section">
    <div class="container center">

        <span class="order-check" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="34" height="34" fill="none"
                 stroke="currentColor" stroke-width="2.6"
                 stroke-linecap="round" stroke-linejoin="round">
                <path d="M5 12.5 L10 17.5 L19 7"/>
            </svg>
        </span>

        <p class="eyebrow">Order Complete</p>
        <h1>주문이 완료됐어요</h1>
        <p class="muted">주문번호 <span class="mono">#${orderId}</span></p>

        <div class="form-card order-summary">
            <c:forEach var="it" items="${orderedItems}">
                <div class="oc-line">
                    <span>${it.productName} <span class="muted">x ${it.qty}</span></span>
                    <span class="mono"><fmt:formatNumber value="${it.subtotal}" type="number"/> 원</span>
                </div>
            </c:forEach>

            <div class="summary-total">
                <span>결제 금액</span>
                <span class="price"><fmt:formatNumber value="${orderTotal}" type="number"/><small> 원</small></span>
            </div>
        </div>

        <div class="order-actions">
            <a href="${ctx}/mypage.do" class="btn btn-forest">주문 내역 보기</a>
            <a href="${ctx}/productList.do" class="btn btn-ghost">쇼핑 계속하기</a>
        </div>

    </div>
</section>

<style>
    .order-check {
        display: inline-grid; place-items: center;
        width: 72px; height: 72px; border-radius: 50%;
        background: rgba(41,74,54,.1); color: var(--forest);
        border: 1.5px solid rgba(41,74,54,.3);
        margin-bottom: 18px;
    }
    .order-summary {
        max-width: 520px; margin: 26px auto 0; text-align: left;
    }
    .order-actions {
        display: flex; justify-content: center; gap: 12px;
        flex-wrap: wrap; margin-top: 26px;
    }
</style>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

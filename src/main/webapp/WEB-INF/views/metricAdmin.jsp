<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "지표 관리 — 관리자"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div class="container">
        <p class="crumb"><a href="${ctx}/main.do">홈</a> · 지표 관리(관리자)</p>
        <h1>지표 관리 <span class="badge badge-soft">관리자</span></h1>
        <p class="muted">판매자가 새로 제안한 지표를 검토·승인하면, 그때부터 소비자에게 상세 설명(도움말)이 열립니다. (테스트용: 별도 관리자 계정 없음)</p>
    </div>
</section>

<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="section-head"><div><p class="eyebrow">Pending</p><h2>검토 대기 지표</h2></div></div>

        <c:set var="anyPending" value="false" />
        <c:forEach var="d" items="${defs}">
            <c:if test="${d.pending}">
                <c:set var="anyPending" value="true" />
                <div class="admin-card">
                    <div class="ac-head">
                        <span class="ac-title">${fn:escapeXml(d.label)}<c:if test="${not empty d.unit}"> <span class="muted">(${fn:escapeXml(d.unit)})</span></c:if></span>
                        <span class="status status-PAID">검토중</span>
                        <span class="muted" style="font-size:.8rem;">제안자 회원 #${d.createdBy}</span>
                    </div>
                    <form method="post" action="${ctx}/metricAdmin.do" class="ac-form">
                        <input type="hidden" name="defId" value="${d.defId}">
                        <input type="hidden" name="act" value="approve">
                        <label class="ac-field">한 줄 요약
                            <input type="text" name="helpSummary" placeholder="이 지표가 무엇인지 한 줄로" required>
                        </label>
                        <label class="ac-field">상세 설명 (일반 텍스트 · 줄바꿈 그대로 표시)
                            <textarea name="helpBody" rows="4" placeholder="조사한 설명을 붙여 넣으세요. 줄바꿈은 그대로 보입니다."></textarea>
                        </label>
                        <div class="ac-grid">
                            <label class="ac-field">값 방향
                                <select name="goodHigh">
                                    <option value="1">높을수록 좋음</option>
                                    <option value="0">낮을수록 좋음</option>
                                </select>
                            </label>
                            <label class="ac-field">게이지 최소
                                <input type="number" step="any" name="gaugeMin" placeholder="예) 0">
                            </label>
                            <label class="ac-field">게이지 최대
                                <input type="number" step="any" name="gaugeMax" placeholder="예) 100">
                            </label>
                        </div>
                        <div class="ac-actions">
                            <button type="submit" class="btn btn-primary btn-sm">승인하고 도움말 부착</button>
                        </div>
                    </form>
                    <form method="post" action="${ctx}/metricAdmin.do" onsubmit="return confirm('이 지표를 거부할까요? 상품에서 숨겨집니다.');" style="margin-top:8px;">
                        <input type="hidden" name="defId" value="${d.defId}">
                        <input type="hidden" name="act" value="reject">
                        <button type="submit" class="btn btn-ghost btn-sm">거부</button>
                    </form>
                </div>
            </c:if>
        </c:forEach>
        <c:if test="${not anyPending}">
            <div class="panel"><p class="muted">검토 대기 중인 새 지표가 없어요.</p></div>
        </c:if>
    </div>
</section>

<section class="section" style="padding-top:0;">
    <div class="container">
        <div class="section-head"><div><p class="eyebrow">Official</p><h2>공식 지표 (카탈로그·승인)</h2></div></div>
        <div class="table-wrap">
            <table class="table">
                <thead><tr><th>지표명</th><th>단위</th><th>품목</th><th>상태</th><th>방향</th><th>도움말</th></tr></thead>
                <tbody>
                    <c:forEach var="d" items="${defs}">
                        <c:if test="${d.official}">
                            <tr>
                                <td>${fn:escapeXml(d.label)}</td>
                                <td>${fn:escapeXml(d.unit)}</td>
                                <td>${empty d.category ? '공통' : d.category}</td>
                                <td><span class="badge badge-soft">${d.status}</span></td>
                                <td>${d.goodHigh ? '높을수록↑' : '낮을수록↓'}</td>
                                <td>${d.hasHelp ? 'O' : '-'}</td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

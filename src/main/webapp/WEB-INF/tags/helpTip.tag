<%@ tag pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="metric" required="true" %>
<%-- 품질 지표 도움말 트리거. 클릭하면 해당 지표 모달(#qh-{metric})이 열린다.
     모달 본문은 common/qualityHelpModals.jsp 에 정의(페이지당 1회 include). --%>
<button type="button" class="help-btn" data-help="${metric}" aria-label="지표 설명 보기">?</button>

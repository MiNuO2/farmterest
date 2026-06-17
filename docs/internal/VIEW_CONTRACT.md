# 팜터레스트 JSP 뷰 작성 계약 (필독)

모든 뷰 JSP는 아래 규칙을 **반드시** 지킨다. 목적: 페이지 간 100% 일관성 + Tomcat 10(jakarta) + 무오류 컴파일.

## 1. 파일 골격 (그대로 사용)

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "페이지제목 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

    ... 페이지 본문 ...

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
```

- `header.jsp` 가 이미 taglib(`c`, `fmt`, `fn`, `ui`)와 `<c:set var="ctx" .../>` 를 선언한다.
  **본문에서 taglib을 다시 선언하지 말 것** (중복 선언 금지). 그냥 `c:`, `fmt:`, `fn:`, `ui:` 바로 사용.
- 자바 스크립틀릿(`<% %>`)은 **위 pageTitle 한 줄만** 허용. 나머지는 전부 EL + JSTL.
- 컨텍스트 경로는 항상 `${ctx}` 사용. 링크 예: `${ctx}/productDetail.do?id=${p.productId}`
- 한글: 쉬운 말, 짧은 문장, 친근한 톤. 깨지는 이모지(🌾🥔 등 astral) 절대 쓰지 말 것 → 아이콘은 SVG나 텍스트로.

## 2. 재사용 태그

상품 카드(그리드 한 칸):
```jsp
<ui:productCard product="${p}" ctx="${ctx}" />
```
추천 카드(근거+점수 표시):
```jsp
<ui:productCard product="${rec.product}" ctx="${ctx}" reason="${rec.reason}" score="${rec.scorePercent}" />
```
품목 비주얼(상세 큰 이미지 대용):
```jsp
<ui:categoryArt category="${product.category}" region="${product.region}" big="true" />
```

가격 출력(원화, 정수):
```jsp
<fmt:formatNumber value="${p.price}" type="number"/> 원
```

## 3. DTO EL 프로퍼티 (게터 → EL 이름)

- **ProductDTO**: productId, sellerId, sellerName, name, category, region, price, stock, imageUrl, description,
  polishedRate(정백도·Integer·null가능), wholeGrainRate(완전립·Integer), moisture(수분·Double), tasteScore(식미치·Integer),
  hasQuality()(=`${product.hasQuality()}` 호출 가능)
- **MemberDTO**: memberId, loginId, name, role, region, `seller`(=isSeller, `${m.seller}`)
- **OrderDTO**: orderId, orderedAt, totalPrice, status, items(List<OrderItemDTO>), itemCount
- **OrderItemDTO**: productId, productName, qty, unitPrice, subtotal
- **CartItem**: productId, productName, category, price, qty, subtotal
- **Recommendation**: product(ProductDTO), score, reason, scorePercent
- **PreferenceProfile**: topCategory, topRegion, avgPolishedRate, avgTasteScore, purchaseCount, `empty`(=isEmpty)
- **SearchCriteria**: keyword, category, region, priceMin, priceMax, minPolishedRate, minWholeGrainRate, minTasteScore, sort, understood

null 가능 값은 `<c:if test="${not empty product.polishedRate}">` 로 감싸 표시.
로그인 회원: `${sessionScope.loginMember}` (이름 `${sessionScope.loginMember.name}`, 판매자여부 `${sessionScope.loginMember.seller}`).

## 4. 선택지 데이터 (필터/폼 공통)

- 품목 category: 쌀, 잡곡, 감자, 채소, 수산
- 지역 region: 철원, 강릉, 평창, 정선, 홍천, 횡성, 영월, 춘천, 속초, 동해, 고성, 양구
- 정렬 sort 값(라벨): relevance(추천순) · price_asc(낮은가격순) · price_desc(높은가격순) · quality(품질좋은순) · newest(신상품순)
- 주문 status: PAID(결제완료) · SHIPPING(배송중) · DONE(완료) · CANCEL(취소)

## 5. 사용 가능한 CSS 클래스 카탈로그 (style.css 에 정의됨 — 새 스타일 만들지 말고 조합)

- 레이아웃: `.container` `.section` `.section-head`(안에 `.eyebrow`,`h2`,`.more`) `.page-head`(안에 `.crumb`,`h1`)
- 버튼: `.btn` + `.btn-primary`(테라코타) `.btn-forest` `.btn-ghost` `.btn-gold` / 크기 `.btn-sm` `.btn-block`
- 상품그리드: `.product-grid`(4열, `.cols-3` 면 3열) — 카드는 `ui:productCard` 사용
- 상점(목록): `.shop-layout`(필터+결과 2단) / 필터 `.filter` `.filter-title` `.filter-group`(접힘 `.collapsed`) `.filter-head`(안 `.arr`) `.filter-body` `.filter-actions` / 결과 `.result-head`(안 `.count` 안 b) `.sort-bar`
- 해석패널(검색): `.understood`(안 `.lab`, `.chip`)
- 품질 성분표(상세): `.spec` `.spec-head` `.spec-row`(3열:라벨/게이지/값) `.spec-label` `.spec-gauge`(안 `<i data-val="0~100">`) `.spec-value`
- 폼/인증: `.form-card` `.auth-wrap` `.auth-card` `.field`(label+input) `.form-grid`(2열) `.role-pick`(label>input+span) / 알림 `.alert`+`.alert-error`/`.alert-ok`
- 테이블: `.table`(숫자칸 `.num`) — 모바일은 `<div class="table-wrap">` 로 감싸기
- 장바구니: `.cart-layout`(목록+요약) `.summary`(안 `.summary-row`, `.summary-total`) 수량 `.qty`(button[data-step="down"/"up"] + input)
- 대시보드(마이페이지): `.dash-grid`(4열) `.stat-card`(안 `.lab`,`.num`,`.sub`) `.panel`(h3) `.tag-list`(안 `.chip`) / 주문 `.order-card`(안 `.oc-head`, `.status`+`.status-DONE`/`.status-PAID`…, `.oc-body`, `.oc-line`)
- 빈 상태: `.empty`(안 `.ic`, h3, p)
- 기타: `.eyebrow` `.muted` `.center` `.mono` `.badge` `.badge-soft` `.rec-reason` `.origin-stamp` `.price`(안 small)

페이지 고유 스타일이 꼭 필요하면 그 JSP 안에 작은 `<style>` 블록으로 한정해서 넣는다(전역 CSS·다른 파일 수정 금지).

## 6. 절대 규칙
- 자기에게 배정된 JSP 파일 **하나만** 생성한다. style.css/header/footer/태그/자바/다른 JSP 를 수정하지 않는다.
- EL 프로퍼티 이름은 위 3절을 정확히 따른다(오타 시 500 에러).
- 레퍼런스로 `WEB-INF/views/main.jsp` 의 구조·톤을 참고한다.

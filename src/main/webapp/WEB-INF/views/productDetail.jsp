<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "상품 상세 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<style>
    /* 상세 2단 레이아웃 (페이지 한정) */
    .detail-layout { display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: 36px; align-items: start; }
    .detail-info h1 { font-size: clamp(1.6rem, 3.4vw, 2.3rem); color: var(--forest); margin: 8px 0 10px; }
    .detail-info .price { font-size: 2rem; display: inline-block; margin: 6px 0 18px; }
    .detail-meta { font-family: var(--mono); font-size: .82rem; color: var(--muted); }
    .detail-meta .dot { display: inline-block; width: 3px; height: 3px; border-radius: 50%; background: var(--muted); vertical-align: middle; margin: 0 8px; }
    .detail-desc { color: var(--ink); line-height: 1.7; margin: 6px 0 22px; }
    .cart-form { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
    .line-total { margin-left: auto; font-size: .9rem; color: var(--muted); white-space: nowrap; }
    .line-total b { font-family: var(--mono); font-size: 1.35rem; color: var(--forest); font-weight: 600; margin-right: 2px; }
    .detail-spec { margin-top: 40px; }
    .spec-title { display: flex; align-items: baseline; gap: 10px; color: var(--forest); font-size: 1.3rem; margin: 0 0 22px; }
    .spec-title span { font-family: var(--mono); font-size: .66rem; letter-spacing: .12em; text-transform: uppercase; color: var(--gold-2); }
    .quality-donuts {
        display: flex; flex-wrap: wrap; justify-content: space-around; align-items: flex-start; gap: 24px;
        background: var(--paper); border: 1px solid var(--line); border-radius: var(--r);
        box-shadow: var(--shadow); padding: 38px 24px;
        transition: opacity .7s ease, transform .7s ease;
    }
    .quality-donuts.reveal-ready { opacity: 0; transform: translateY(16px); }
    .quality-donuts.in { opacity: 1; transform: none; }
    .donut { width: 172px; text-align: center; }
    .donut-ring { position: relative; width: 150px; height: 150px; margin: 0 auto; }
    .donut-ring svg { width: 150px; height: 150px; transform: rotate(-90deg); }
    .dt-track { fill: none; stroke: var(--paper-3); stroke-width: 9; }
    .dt-fill { fill: none; stroke-width: 9; stroke-dasharray: 226.19; transition: stroke-dashoffset 1.2s cubic-bezier(.2,.8,.2,1); }
    .donut-num { position: absolute; inset: 0; text-align: center; line-height: 150px; white-space: nowrap; }
    .donut-num b { font-family: var(--serif); font-size: 2.6rem; color: var(--ink); }
    .donut-num i { font-style: normal; font-size: .95rem; color: var(--muted); margin-left: 2px; }
    .donut-cap { margin-top: 14px; font-size: 1rem; font-weight: 600; color: var(--forest); }
    .dt-gold   .dt-fill { stroke: var(--gold); }
    .dt-forest .dt-fill { stroke: var(--moss); }
    .dt-clay   .dt-fill { stroke: var(--clay); }
    .dt-teal   .dt-fill { stroke: #3E8A9A; }
    @media (max-width: 760px) {
        .detail-layout { grid-template-columns: 1fr; gap: 24px; }
        .quality-donuts { gap: 18px; padding: 26px 12px; }
        .donut { width: 44%; }
        .donut-ring, .donut-ring svg { width: 128px; height: 128px; }
        .donut-num { line-height: 128px; }
        .donut-num b { font-size: 2.2rem; }
    }
</style>

<section class="section">
    <div class="container">

        <div class="page-head" style="padding-bottom:0;">
            <div class="crumb">
                <a href="${ctx}/main.do">홈</a> /
                <a href="${ctx}/productList.do">전체상품</a> /
                ${product.category}
            </div>
        </div>

        <!-- 상단 2단: 왼쪽 비주얼 / 오른쪽 정보 -->
        <div class="detail-layout">
            <div class="detail-art">
                <ui:categoryArt category="${product.category}" region="${product.region}" big="true"
                                imageUrl="${product.imageUrl}" ctx="${ctx}" name="${product.name}" />
            </div>

            <div class="detail-info">
                <h1>${product.name}</h1>
                <p class="detail-meta">
                    <span>${product.sellerName}</span><span class="dot"></span><span>강원 ${product.region}</span>
                </p>
                <c:if test="${product.reviewCount > 0}">
                    <p class="detail-rating"><ui:stars rating="${product.avgRating}" count="${product.reviewCount}" /></p>
                </c:if>
                <span class="price"><fmt:formatNumber value="${product.price}" type="number"/><small> 원</small></span>

                <p class="detail-desc">${product.description}</p>

                <form class="cart-form" action="${ctx}/cartAdd.do" method="get" data-price="${product.price}">
                    <input type="hidden" name="id" value="${product.productId}">
                    <div class="qty">
                        <button type="button" data-step="down" aria-label="수량 줄이기">−</button>
                        <input type="text" name="qty" value="1" inputmode="numeric">
                        <button type="button" data-step="up" aria-label="수량 늘리기">+</button>
                    </div>
                    <div class="line-total">합계 <b id="lineTotal"><fmt:formatNumber value="${product.price}" type="number"/></b> 원</div>
                    <button type="submit" class="btn btn-primary">장바구니 담기</button>
                </form>
            </div>
        </div>

        <!-- 품질 지표 (도넛 게이지: 가운데에 수치) -->
        <div class="detail-spec">
            <c:choose>
                <c:when test="${product.hasQuality()}">
                    <h3 class="spec-title">품질 지표 <span>강원 산지 검증</span></h3>
                    <div class="quality-donuts">
                        <c:if test="${not empty product.polishedRate}">
                            <div class="donut dt-gold">
                                <div class="donut-ring">
                                    <svg viewBox="0 0 84 84">
                                        <circle class="dt-track" cx="42" cy="42" r="36"/>
                                        <circle class="dt-fill" cx="42" cy="42" r="36" data-pct="${product.polishedRate}" style="stroke-dashoffset:${226.19 - product.polishedRate * 2.2619}"/>
                                    </svg>
                                    <div class="donut-num"><b>${product.polishedRate}</b><i>%</i></div>
                                </div>
                                <div class="donut-cap">정백도 <ui:helpTip metric="polished" /></div>
                            </div>
                        </c:if>
                        <c:if test="${not empty product.wholeGrainRate}">
                            <div class="donut dt-forest">
                                <div class="donut-ring">
                                    <svg viewBox="0 0 84 84">
                                        <circle class="dt-track" cx="42" cy="42" r="36"/>
                                        <circle class="dt-fill" cx="42" cy="42" r="36" data-pct="${product.wholeGrainRate}" style="stroke-dashoffset:${226.19 - product.wholeGrainRate * 2.2619}"/>
                                    </svg>
                                    <div class="donut-num"><b>${product.wholeGrainRate}</b><i>%</i></div>
                                </div>
                                <div class="donut-cap">완전립 <ui:helpTip metric="whole" /></div>
                            </div>
                        </c:if>
                        <c:if test="${not empty product.tasteScore}">
                            <div class="donut dt-clay">
                                <div class="donut-ring">
                                    <svg viewBox="0 0 84 84">
                                        <circle class="dt-track" cx="42" cy="42" r="36"/>
                                        <circle class="dt-fill" cx="42" cy="42" r="36" data-pct="${product.tasteScore}" style="stroke-dashoffset:${226.19 - product.tasteScore * 2.2619}"/>
                                    </svg>
                                    <div class="donut-num"><b>${product.tasteScore}</b><i>점</i></div>
                                </div>
                                <div class="donut-cap">식미치 <ui:helpTip metric="taste" /></div>
                            </div>
                        </c:if>
                        <c:if test="${not empty product.moisture}">
                            <div class="donut dt-teal">
                                <div class="donut-ring">
                                    <svg viewBox="0 0 84 84">
                                        <circle class="dt-track" cx="42" cy="42" r="36"/>
                                        <circle class="dt-fill" cx="42" cy="42" r="36" data-pct="${product.moisture * 5}" style="stroke-dashoffset:${226.19 - product.moisture * 5 * 2.2619}"/>
                                    </svg>
                                    <div class="donut-num"><b><fmt:formatNumber value="${product.moisture}" minFractionDigits="1" maxFractionDigits="1"/></b><i>%</i></div>
                                </div>
                                <div class="donut-cap">수분 <ui:helpTip metric="moisture" /></div>
                            </div>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:if test="${not product.hasMetrics}"><p class="muted">이 상품은 아직 품질 지표가 등록되지 않았어요.</p></c:if>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</section>

<!-- 유연한 품질 지표 (product_metric) -->
<c:if test="${product.hasMetrics}">
    <section class="section" style="padding-top:8px;">
        <div class="container">
            <h3 class="spec-title">품질 지표 <span>숫자로 비교하세요</span></h3>
            <div class="metric-spec">
                <c:forEach var="m" items="${product.metrics}">
                    <div class="metric-item">
                        <div class="mi-head">
                            <span class="mi-label">${fn:escapeXml(m.def.label)}</span>
                            <c:choose>
                                <c:when test="${m.def.hasHelp}"><button type="button" class="help-btn" data-help="def-${m.def.defId}" aria-label="지표 설명 보기">?</button></c:when>
                                <c:when test="${m.def.pending}"><span class="mi-pending">관리자 검토중</span></c:when>
                            </c:choose>
                            <span class="mi-value">${fn:escapeXml(m.value)}<c:if test="${not empty m.def.unit}"> ${fn:escapeXml(m.def.unit)}</c:if></span>
                        </div>
                        <c:if test="${not empty m.gaugePercent}">
                            <div class="mi-bar ${m.def.goodHigh ? '' : 'rev'}"><span style="width:${m.gaugePercent}%"></span></div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
            <p class="muted" style="font-size:.8rem; margin-top:10px;">'관리자 검토중'인 지표는 판매자가 새로 제안한 항목으로, 승인되면 상세 설명이 열립니다.</p>
        </div>
    </section>

    <%-- 지표 도움말 모달 (공식 지표만) --%>
    <c:forEach var="m" items="${product.metrics}">
        <c:if test="${m.def.hasHelp}">
            <div class="qh-modal" id="qh-def-${m.def.defId}" role="dialog" aria-modal="true">
                <div class="qh-dialog">
                    <div class="qh-head">
                        <h3>${fn:escapeXml(m.def.label)}<c:if test="${not empty m.def.unit}"> <span style="font-size:.66em; color:var(--muted);">(${fn:escapeXml(m.def.unit)})</span></c:if></h3>
                        <button type="button" class="qh-x" data-qh-close aria-label="닫기">&times;</button>
                    </div>
                    <div class="qh-body">
                        <p class="qh-summary">${fn:escapeXml(m.def.helpSummary)}</p>
                        <%-- 카탈로그(신뢰된 시드 HTML)만 원문 렌더. 관리자 승인 커스텀 설명은 escape(저장형 XSS 차단) --%>
                        <c:choose>
                            <c:when test="${m.def.status == 'CATALOG'}">${m.def.helpBody}</c:when>
                            <c:otherwise><p style="white-space:pre-line;">${fn:escapeXml(m.def.helpBody)}</p></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="qh-foot"><button type="button" class="btn btn-forest btn-sm" data-qh-close>닫기</button></div>
                </div>
            </div>
        </c:if>
    </c:forEach>
</c:if>

<!-- 상품 후기 -->
<section class="section" style="padding-top:8px;">
    <div class="container">
        <div class="section-head">
            <div>
                <p class="eyebrow">Reviews</p>
                <h2>상품 후기 <c:if test="${product.reviewCount > 0}"><span class="count-inline">${product.reviewCount}개</span></c:if></h2>
            </div>
            <c:if test="${product.reviewCount > 0}">
                <span class="rv-avg-head"><ui:stars rating="${product.avgRating}" count="${product.reviewCount}" /></span>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${not empty reviews}">
                <div class="review-list">
                    <c:forEach var="rv" items="${reviews}">
                        <div class="review-item">
                            <div class="rv-top">
                                <ui:stars stars="${rv.rating}" size="sm" />
                                <span class="rv-who">${fn:escapeXml(rv.memberName)}</span>
                                <span class="rv-date"><fmt:formatDate value="${rv.createdAt}" pattern="yyyy.MM.dd"/></span>
                            </div>
                            <c:if test="${not empty rv.comment}"><p class="rv-cmt">${fn:escapeXml(rv.comment)}</p></c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="panel">
                    <p class="muted">아직 후기가 없어요. 이 상품을 구매하면 마이페이지에서 별점을 남길 수 있어요.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<!-- 같은 품목 다른 상품 -->
<c:if test="${not empty related}">
    <section class="section" style="background:var(--paper-2); border-top:1px solid var(--line);">
        <div class="container">
            <div class="section-head">
                <div>
                    <p class="eyebrow">More in ${product.category}</p>
                    <h2>같은 품목 다른 상품</h2>
                </div>
            </div>
            <div class="product-grid cols-3">
                <c:forEach var="p" items="${related}">
                    <ui:productCard product="${p}" ctx="${ctx}" />
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>

<script>
    /* 수량 변동에 따라 합계(수량 x 단가) 실시간 표시 */
    (function () {
        var form = document.querySelector('.cart-form');
        if (!form) return;
        var qty = form.querySelector('input[name="qty"]');
        var totalEl = document.getElementById('lineTotal');
        var price = parseInt(form.getAttribute('data-price'), 10) || 0;
        function update() {
            var n = parseInt(qty.value, 10);
            if (!n || n < 1) n = 1;
            totalEl.textContent = (n * price).toLocaleString('ko-KR');
        }
        qty.addEventListener('input', update);
        qty.addEventListener('change', update);
        update();
    })();

    /* 품질지표 도넛: 화면에 들어오는 순간 페이드인 + 게이지 채움 애니메이션 */
    (function () {
        var box = document.querySelector('.quality-donuts');
        if (!box) return;
        var fills = box.querySelectorAll('.dt-fill');
        function reveal() {
            box.classList.remove('reveal-ready');
            box.classList.add('in');
            fills.forEach(function (el) {
                if (el.dataset.target !== undefined) el.style.strokeDashoffset = el.dataset.target;
            });
        }
        try {
            box.classList.add('reveal-ready');                 // 투명 + 빈 게이지 준비
            fills.forEach(function (el) {
                el.dataset.target = el.style.strokeDashoffset || '0';  // EL이 넣은 최종값 보관
                el.style.transition = 'none';
                el.style.strokeDashoffset = '226.19';          // 빈(0%) 상태
            });
            void box.offsetWidth;                               // 리플로우(빈 상태 확정)
            fills.forEach(function (el) { el.style.transition = ''; });
            if ('IntersectionObserver' in window) {
                var io = new IntersectionObserver(function (entries) {
                    entries.forEach(function (e) { if (e.isIntersecting) { reveal(); io.disconnect(); } });
                }, { threshold: 0.25 });
                io.observe(box);
            } else {
                reveal();
            }
        } catch (err) {
            reveal();   // 문제가 생기면 정적으로라도 보이게
        }
    })();
</script>

<%@ include file="/WEB-INF/views/common/qualityHelpModals.jsp" %>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>

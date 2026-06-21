<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "상품 등록·수정 — 팜터레스트"); %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="page-head">
    <div class="container">
        <div class="crumb">
            <a href="${ctx}/sellerProducts.do">판매자센터</a> · 상품 관리
        </div>
        <c:choose>
            <c:when test="${not empty product}">
                <h1>상품 수정</h1>
            </c:when>
            <c:otherwise>
                <h1>상품 등록</h1>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<section class="section" style="padding-top:8px;">
    <div class="container" style="max-width:720px;">
        <div class="form-card">
            <form method="post" action="${ctx}/sellerProductSave.do" enctype="multipart/form-data">
                <input type="hidden" name="productId" value="${not empty product ? product.productId : ''}">
                <input type="hidden" name="currentImageUrl" value="${fn:escapeXml(product.imageUrl)}">

                <div class="field">
                    <label for="name">상품명</label>
                    <input type="text" id="name" name="name" value="${fn:escapeXml(product.name)}"
                           placeholder="예) 철원 오대쌀 10kg" required>
                </div>

                <div class="form-grid">
                    <div class="field">
                        <label for="category">품목</label>
                        <select id="category" name="category" required>
                            <option value="쌀" ${product.category == '쌀' ? 'selected' : ''}>쌀</option>
                            <option value="잡곡" ${product.category == '잡곡' ? 'selected' : ''}>잡곡</option>
                            <option value="감자" ${product.category == '감자' ? 'selected' : ''}>감자</option>
                            <option value="채소" ${product.category == '채소' ? 'selected' : ''}>채소</option>
                            <option value="수산" ${product.category == '수산' ? 'selected' : ''}>수산</option>
                        </select>
                    </div>
                    <div class="field">
                        <label for="region">지역</label>
                        <select id="region" name="region" required>
                            <option value="철원" ${product.region == '철원' ? 'selected' : ''}>철원</option>
                            <option value="강릉" ${product.region == '강릉' ? 'selected' : ''}>강릉</option>
                            <option value="평창" ${product.region == '평창' ? 'selected' : ''}>평창</option>
                            <option value="정선" ${product.region == '정선' ? 'selected' : ''}>정선</option>
                            <option value="홍천" ${product.region == '홍천' ? 'selected' : ''}>홍천</option>
                            <option value="횡성" ${product.region == '횡성' ? 'selected' : ''}>횡성</option>
                            <option value="영월" ${product.region == '영월' ? 'selected' : ''}>영월</option>
                            <option value="춘천" ${product.region == '춘천' ? 'selected' : ''}>춘천</option>
                            <option value="속초" ${product.region == '속초' ? 'selected' : ''}>속초</option>
                            <option value="동해" ${product.region == '동해' ? 'selected' : ''}>동해</option>
                            <option value="고성" ${product.region == '고성' ? 'selected' : ''}>고성</option>
                            <option value="양구" ${product.region == '양구' ? 'selected' : ''}>양구</option>
                        </select>
                    </div>
                </div>

                <div class="form-grid">
                    <div class="field">
                        <label for="price">가격 (원)</label>
                        <input type="number" id="price" name="price" min="0" step="1"
                               value="${product.price}" placeholder="예) 32000" required>
                    </div>
                    <div class="field">
                        <label for="stock">재고 (개)</label>
                        <input type="number" id="stock" name="stock" min="0" step="1"
                               value="${product.stock}" placeholder="예) 50" required>
                    </div>
                </div>

                <div class="field">
                    <label for="imageFile">상품 이미지 (선택)</label>
                    <c:if test="${not empty product.imageUrl}">
                        <div class="img-current">
                            <img src="${ctx}/${product.imageUrl}" alt="현재 이미지" class="img-thumb"
                                 onerror="this.closest('.img-current').style.display='none'">
                            <span class="muted">현재 이미지 · 새 파일을 올리면 교체됩니다</span>
                        </div>
                    </c:if>
                    <input type="file" id="imageFile" name="imageFile" accept="image/*">
                    <div class="img-current" id="imgPreviewWrap" hidden>
                        <img alt="미리보기" class="img-thumb" id="imgPreview">
                        <span class="muted">새로 올릴 이미지 미리보기</span>
                    </div>
                    <p class="muted" style="font-size:.82rem; margin-top:6px;">JPG·PNG·WEBP 등 이미지 파일(최대 5MB). 비워두면 품목 그림으로 보여드려요.</p>
                </div>

                <div class="field">
                    <label for="description">설명</label>
                    <textarea id="description" name="description"
                              placeholder="산지, 재배 방법, 보관 팁 등을 편하게 적어주세요.">${fn:escapeXml(product.description)}</textarea>
                </div>

                <h3 style="color:var(--forest); margin-top:8px;">품질 지표 (선택)</h3>
                <p class="muted" style="font-size:.86rem; margin-top:-4px;">
                    위에서 고른 <b>품목</b>에 맞는 추천 지표를 눌러 담거나, 나만의 지표를 직접 추가할 수 있어요.
                    직접 만든 새 지표는 <b>관리자 승인</b> 후 소비자에게 상세 설명(도움말)이 노출됩니다.
                </p>

                <div class="metric-suggest" id="metricSuggest"></div>
                <div class="metric-rows" id="metricRows"></div>
                <button type="button" class="btn btn-ghost btn-sm" id="addMetricBtn" style="margin-top:8px;">+ 직접 지표 추가</button>

                <div style="display:flex; gap:10px; margin-top:18px;">
                    <button type="submit" class="btn btn-primary">저장</button>
                    <a href="${ctx}/sellerProducts.do" class="btn btn-ghost">취소</a>
                </div>
            </form>
        </div>
    </div>
</section>

<script>
  /* 이미지 파일 선택 시 미리보기 */
  (function () {
    var input = document.getElementById("imageFile");
    if (!input) return;
    input.addEventListener("change", function () {
      var wrap = document.getElementById("imgPreviewWrap");
      var img = document.getElementById("imgPreview");
      var f = input.files && input.files[0];
      if (f && img && wrap) { img.src = URL.createObjectURL(f); wrap.hidden = false; }
      else if (wrap) { wrap.hidden = true; }
    });
  })();

  /* 유연한 지표 편집기: 품목별 추천칩 + 직접추가 + 삭제 */
  (function () {
    var SUG = ${suggestionsJson};                 // {품목: [{key,label,unit}], 공통:[...]}
    var EXISTING = ${existingRowsJson};   // Gson 직렬화(따옴표·역슬래시 안전)
    var rowsEl = document.getElementById("metricRows");
    var sugEl = document.getElementById("metricSuggest");
    var catSel = document.getElementById("category");
    var addBtn = document.getElementById("addMetricBtn");
    if (!rowsEl || !sugEl || !catSel) return;

    function esc(s){ return (s==null?"":String(s)).replace(/&/g,"&amp;").replace(/"/g,"&quot;").replace(/</g,"&lt;"); }

    function addRow(key, label, value, unit) {
      var div = document.createElement("div");
      div.className = "metric-row";
      var locked = key && key.length ? "readonly" : "";
      div.innerHTML =
        '<input type="hidden" name="metricKey" value="' + esc(key) + '">' +
        '<input type="text" name="metricLabel" class="m-label" maxlength="40" placeholder="지표명" value="' + esc(label) + '" ' + locked + '>' +
        '<input type="text" name="metricValue" class="m-value" maxlength="40" placeholder="값" value="' + esc(value) + '">' +
        '<input type="text" name="metricUnit" class="m-unit" maxlength="20" placeholder="단위" value="' + esc(unit) + '" ' + locked + '>' +
        '<button type="button" class="m-remove" aria-label="삭제">&times;</button>';
      div.querySelector(".m-remove").addEventListener("click", function(){ div.remove(); });
      rowsEl.appendChild(div);
      return div;
    }

    function renderSuggest() {
      sugEl.innerHTML = "";
      var list = (SUG[catSel.value] || []).concat(SUG["공통"] || []);
      if (!list.length) { sugEl.innerHTML = '<span class="muted" style="font-size:.82rem;">이 품목엔 아직 추천 지표가 없어요. 직접 추가해 보세요.</span>'; return; }
      list.forEach(function(s){
        var b = document.createElement("button");
        b.type = "button"; b.className = "sug-chip";
        b.textContent = "+ " + s.label + (s.unit ? (" (" + s.unit + ")") : "");
        b.addEventListener("click", function(){ addRow(s.key, s.label, "", s.unit); });
        sugEl.appendChild(b);
      });
    }

    catSel.addEventListener("change", renderSuggest);
    if (addBtn) addBtn.addEventListener("click", function(){ addRow("", "", "", "").querySelector(".m-label").focus(); });
    renderSuggest();
    EXISTING.forEach(function(r){ addRow(r.key, r.label, r.value, r.unit); });
  })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

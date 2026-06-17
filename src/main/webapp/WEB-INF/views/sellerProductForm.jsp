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
            <form method="post" action="${ctx}/sellerProductSave.do">
                <input type="hidden" name="productId" value="${not empty product ? product.productId : ''}">

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
                    <label for="imageUrl">이미지 URL (선택)</label>
                    <input type="text" id="imageUrl" name="imageUrl" value="${fn:escapeXml(product.imageUrl)}"
                           placeholder="비워두면 품목 그림으로 보여드려요">
                </div>

                <div class="field">
                    <label for="description">설명</label>
                    <textarea id="description" name="description"
                              placeholder="산지, 재배 방법, 보관 팁 등을 편하게 적어주세요.">${fn:escapeXml(product.description)}</textarea>
                </div>

                <h3 style="color:var(--forest); margin-top:8px;">품질 지표 (쌀·잡곡 권장, 선택)</h3>
                <p class="muted" style="font-size:.86rem; margin-top:-4px;">
                    모두 선택 항목이에요. 주로 쌀·잡곡에서 채워주시면 구매자가 숫자로 비교하기 좋아요. 비워두셔도 됩니다.
                </p>

                <div class="form-grid">
                    <div class="field">
                        <label for="polishedRate">정백도 (%)</label>
                        <input type="number" id="polishedRate" name="polishedRate" min="0" max="100" step="1"
                               value="${product.polishedRate}" placeholder="예) 92">
                    </div>
                    <div class="field">
                        <label for="wholeGrainRate">완전립 (%)</label>
                        <input type="number" id="wholeGrainRate" name="wholeGrainRate" min="0" max="100" step="1"
                               value="${product.wholeGrainRate}" placeholder="예) 95">
                    </div>
                </div>

                <div class="form-grid">
                    <div class="field">
                        <label for="moisture">수분 (%)</label>
                        <input type="number" id="moisture" name="moisture" min="0" max="100" step="0.1"
                               value="${product.moisture}" placeholder="예) 14.5">
                    </div>
                    <div class="field">
                        <label for="tasteScore">식미치</label>
                        <input type="number" id="tasteScore" name="tasteScore" min="0" max="100" step="1"
                               value="${product.tasteScore}" placeholder="예) 82">
                    </div>
                </div>

                <div style="display:flex; gap:10px; margin-top:18px;">
                    <button type="submit" class="btn btn-primary">저장</button>
                    <a href="${ctx}/sellerProducts.do" class="btn btn-ghost">취소</a>
                </div>
            </form>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

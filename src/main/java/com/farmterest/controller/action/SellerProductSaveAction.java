package com.farmterest.controller.action;

import java.util.ArrayList;
import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.MetricDefinitionDAO;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dao.ProductMetricDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.MetricDefinition;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.ProductMetric;
import com.farmterest.util.ImageStore;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/** 판매자: 상품 등록/수정 저장 (이미지 업로드 + 유연한 지표 편집). */
public class SellerProductSaveAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null || !member.isSeller()) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        ProductDTO p = new ProductDTO();
        p.setSellerId(member.getMemberId());
        p.setName(Params.str(request, "name"));
        p.setCategory(Params.str(request, "category"));
        p.setRegion(Params.str(request, "region"));
        p.setPrice(Params.intOr(request, "price", 0));
        p.setStock(Params.intOr(request, "stock", 0));

        // 이미지: 새 업로드가 있으면 교체, 없으면 기존 유지(수정 시)
        String imageUrl = Params.str(request, "currentImageUrl");
        try {
            Part filePart = request.getPart("imageFile");
            String uploaded = ImageStore.save(filePart);
            if (uploaded != null) {
                imageUrl = uploaded;
            }
        } catch (Exception ignore) {
            // 업로드 실패는 무시
        }
        p.setImageUrl(imageUrl);
        p.setDescription(Params.str(request, "description"));

        // ----- 유연한 지표 행 파싱 -----
        // metricKey: 숫자=기존 def_id, "col:xxx"=쌀 레거시 컬럼, 빈값=커스텀(신규 PENDING)
        List<ProductMetric> productMetrics = new ArrayList<>();
        MetricDefinitionDAO defDao = new MetricDefinitionDAO();
        String[] keys = request.getParameterValues("metricKey");
        String[] labels = request.getParameterValues("metricLabel");
        String[] values = request.getParameterValues("metricValue");
        String[] units = request.getParameterValues("metricUnit");
        if (labels != null) {
            for (int i = 0; i < labels.length; i++) {
                String key = keys != null && i < keys.length ? keys[i] : null;
                String label = i < labels.length ? safe(labels[i]) : null;
                String val = values != null && i < values.length ? safe(values[i]) : null;
                String unit = units != null && i < units.length ? safe(units[i]) : null;
                if (label == null || val == null) {
                    continue;   // 라벨·값이 비면 무시
                }
                if (key != null && key.startsWith("col:")) {
                    applyRiceColumn(p, key.substring(4), val);
                } else {
                    int defId = 0;
                    if (key != null && key.matches("\\d+")) {
                        // 기존 공식 지표: 실제 존재하고 거부되지 않은 것만 허용(위조 def_id 차단 → FK 위반 방지)
                        MetricDefinition existing = defDao.findById(Integer.parseInt(key));
                        if (existing != null && !"REJECTED".equals(existing.getStatus())) {
                            defId = existing.getDefId();
                        }
                    } else {
                        defId = defDao.findOrCreateCustom(label, unit, p.getCategory(), member.getMemberId());  // 커스텀 → PENDING
                    }
                    if (defId > 0) {
                        ProductMetric m = new ProductMetric();
                        MetricDefinition d = new MetricDefinition();
                        d.setDefId(defId);
                        m.setDef(d);
                        m.setValue(val);
                        productMetrics.add(m);
                    }
                }
            }
        }

        ProductDAO dao = new ProductDAO();
        int productId = Params.intOr(request, "productId", 0);
        boolean owned;
        if (productId > 0) {
            p.setProductId(productId);
            owned = dao.update(p);          // WHERE seller_id 일치 → 본인 상품일 때만 true
        } else {
            productId = dao.insert(p);
            owned = productId > 0;
        }
        // 본인 상품(또는 신규)일 때만 지표 교체 — 남의 상품 지표 삭제/덮어쓰기 방지
        if (owned && productId > 0) {
            new ProductMetricDAO().replaceForProduct(productId, productMetrics);
        }
        return ActionForward.redirect(request.getContextPath() + "/sellerProducts.do");
    }

    /** 쌀 레거시 컬럼(필터·추천 호환) 채우기. */
    private void applyRiceColumn(ProductDTO p, String col, String val) {
        switch (col) {
            case "polishedRate":   p.setPolishedRate(toInt(val)); break;
            case "wholeGrainRate": p.setWholeGrainRate(toInt(val)); break;
            case "tasteScore":     p.setTasteScore(toInt(val)); break;
            case "moisture":       p.setMoisture(toDbl(val)); break;
            default: /* 무시 */ break;
        }
    }

    private static String safe(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static Integer toInt(String s) {
        try { return Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
    }

    private static Double toDbl(String s) {
        try { return Double.valueOf(s.trim()); } catch (Exception e) { return null; }
    }
}

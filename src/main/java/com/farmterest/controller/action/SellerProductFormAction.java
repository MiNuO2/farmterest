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
import com.farmterest.util.Params;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 판매자: 상품 등록/수정 폼. 유연한 지표 편집기용 데이터 준비. */
public class SellerProductFormAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MemberDTO member = (MemberDTO) request.getSession().getAttribute(Params.LOGIN);
        if (member == null || !member.isSeller()) {
            return ActionForward.redirect(request.getContextPath() + "/login.do");
        }

        // 편집 시 기존 지표 행: {key, label, value, unit}
        List<String[]> existingRows = new ArrayList<>();
        int id = Params.intOr(request, "id", 0);
        if (id > 0) {
            ProductDTO product = new ProductDAO().findById(id);
            if (product != null && product.getSellerId() == member.getMemberId()) {
                request.setAttribute("product", product);
                if (product.getPolishedRate() != null)
                    existingRows.add(new String[]{"col:polishedRate", "정백도", String.valueOf(product.getPolishedRate()), "%"});
                if (product.getWholeGrainRate() != null)
                    existingRows.add(new String[]{"col:wholeGrainRate", "완전립", String.valueOf(product.getWholeGrainRate()), "%"});
                if (product.getTasteScore() != null)
                    existingRows.add(new String[]{"col:tasteScore", "식미치", String.valueOf(product.getTasteScore()), ""});
                if (product.getMoisture() != null)
                    existingRows.add(new String[]{"col:moisture", "수분", String.valueOf(product.getMoisture()), "%"});
                for (ProductMetric m : new ProductMetricDAO().findByProduct(id)) {
                    String unit = m.getDef().getUnit() == null ? "" : m.getDef().getUnit();
                    existingRows.add(new String[]{String.valueOf(m.getDef().getDefId()), m.getDef().getLabel(), m.getValue(), unit});
                }
            }
        }
        // JS로 안전하게 임베드하기 위해 JSON 직렬화(따옴표·역슬래시·유니코드 이스케이프)
        JsonArray exJson = new JsonArray();
        for (String[] r : existingRows) {
            JsonObject o = new JsonObject();
            o.addProperty("key", r[0]);
            o.addProperty("label", r[1]);
            o.addProperty("value", r[2]);
            o.addProperty("unit", r[3]);
            exJson.add(o);
        }
        request.setAttribute("existingRowsJson", exJson.toString());

        // 카테고리별 추천 지표 JSON (드롭다운 선택 시 JS가 칩으로 제안)
        JsonObject sug = new JsonObject();
        JsonArray rice = new JsonArray();
        rice.add(sugObj("col:polishedRate", "정백도", "%"));
        rice.add(sugObj("col:wholeGrainRate", "완전립", "%"));
        rice.add(sugObj("col:tasteScore", "식미치", ""));
        rice.add(sugObj("col:moisture", "수분", "%"));
        sug.add("쌀", rice);

        JsonArray common = new JsonArray();
        for (MetricDefinition d : new MetricDefinitionDAO().findAll()) {
            if (!d.isOfficial()) {
                continue;
            }
            JsonObject o = sugObj(String.valueOf(d.getDefId()), d.getLabel(), d.getUnit() == null ? "" : d.getUnit());
            if (d.getCategory() == null) {
                common.add(o);
            } else {
                if (!sug.has(d.getCategory())) {
                    sug.add(d.getCategory(), new JsonArray());
                }
                sug.getAsJsonArray(d.getCategory()).add(o);
            }
        }
        sug.add("공통", common);
        request.setAttribute("suggestionsJson", sug.toString());

        return ActionForward.forward("/WEB-INF/views/sellerProductForm.jsp");
    }

    private static JsonObject sugObj(String key, String label, String unit) {
        JsonObject o = new JsonObject();
        o.addProperty("key", key);
        o.addProperty("label", label);
        o.addProperty("unit", unit);
        return o;
    }
}

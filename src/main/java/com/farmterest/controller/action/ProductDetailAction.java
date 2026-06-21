package com.farmterest.controller.action;

import java.util.List;
import java.util.stream.Collectors;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dao.ProductMetricDAO;
import com.farmterest.model.dao.ReviewDAO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 상품 상세 + 같은 품목 추천. */
public class ProductDetailAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Params.intOr(request, "id", 0);
        ProductDAO dao = new ProductDAO();
        ProductDTO product = dao.findById(id);
        if (product == null) {
            request.setAttribute("error", "상품을 찾을 수 없습니다.");
            return ActionForward.forward("/WEB-INF/views/error.jsp");
        }
        product.setMetrics(new ProductMetricDAO().findByProduct(id));
        request.setAttribute("product", product);

        // 같은 품목 다른 상품 (자기 자신 제외, 최대 4개)
        SearchCriteria c = new SearchCriteria();
        c.setCategory(product.getCategory());
        List<ProductDTO> related = dao.search(c).stream()
                .filter(p -> p.getProductId() != product.getProductId())
                .limit(4)
                .collect(Collectors.toList());
        request.setAttribute("related", related);

        // 이 상품의 후기 목록(별점 평균은 product 에 이미 집계됨)
        request.setAttribute("reviews", new ReviewDAO().findByProduct(id));

        return ActionForward.forward("/WEB-INF/views/productDetail.jsp");
    }
}

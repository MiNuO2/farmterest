package com.farmterest.controller.action;

import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.Params;
import com.farmterest.util.SearchCriteria;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 상품 목록 + 다중 상세필터 (동적 SQL). */
public class ProductListAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SearchCriteria c = new SearchCriteria();
        c.setKeyword(Params.str(request, "keyword"));
        c.setCategory(Params.str(request, "category"));
        c.setRegion(Params.str(request, "region"));
        c.setPriceMin(Params.intObj(request, "priceMin"));
        c.setPriceMax(Params.intObj(request, "priceMax"));
        c.setMinPolishedRate(Params.intObj(request, "minPolishedRate"));
        c.setMinWholeGrainRate(Params.intObj(request, "minWholeGrainRate"));
        c.setMinTasteScore(Params.intObj(request, "minTasteScore"));
        String sort = Params.str(request, "sort");
        if (sort != null) {
            c.setSort(sort);
        }

        List<ProductDTO> products = new ProductDAO().search(c);
        request.setAttribute("products", products);
        request.setAttribute("criteria", c);
        request.setAttribute("resultCount", products.size());
        return ActionForward.forward("/WEB-INF/views/productList.jsp");
    }
}

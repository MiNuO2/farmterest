package com.farmterest.controller.action;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;
import com.farmterest.service.BestProductService;
import com.farmterest.service.SeasonService;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 품목(카테고리) 페이지: 이달의 인기(최고) 상품을 크게 + 해당 품목 상품 목록. */
public class CategoryAction implements Action {

    private static final DateTimeFormatter YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String category = Params.str(request, "cat");
        if (category == null || !SeasonService.CATEGORIES.contains(category)) {
            return ActionForward.redirect(request.getContextPath() + "/productList.do");
        }

        String yyyymm = LocalDate.now().format(YYYYMM);
        List<ProductDTO> products = new ProductDAO().findByCategoryWithStats(category, yyyymm);
        Recommendation best = new BestProductService().pickBest(products);

        request.setAttribute("category", category);
        request.setAttribute("products", products);
        request.setAttribute("resultCount", products.size());
        request.setAttribute("best", best);
        request.setAttribute("seasonal", SeasonService.seasonalCategories().contains(category));
        request.setAttribute("seasonalMap", SeasonService.seasonalFlags());
        return ActionForward.forward("/WEB-INF/views/category.jsp");
    }
}

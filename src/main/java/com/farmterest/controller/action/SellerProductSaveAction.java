package com.farmterest.controller.action;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.model.dao.ProductDAO;
import com.farmterest.model.dto.MemberDTO;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 판매자: 상품 등록/수정 저장. */
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
        p.setImageUrl(Params.str(request, "imageUrl"));
        p.setDescription(Params.str(request, "description"));
        p.setPolishedRate(Params.intObj(request, "polishedRate"));
        p.setWholeGrainRate(Params.intObj(request, "wholeGrainRate"));
        p.setMoisture(Params.dblObj(request, "moisture"));
        p.setTasteScore(Params.intObj(request, "tasteScore"));

        ProductDAO dao = new ProductDAO();
        int productId = Params.intOr(request, "productId", 0);
        if (productId > 0) {
            p.setProductId(productId);
            dao.update(p);
        } else {
            dao.insert(p);
        }
        return ActionForward.redirect(request.getContextPath() + "/sellerProducts.do");
    }
}

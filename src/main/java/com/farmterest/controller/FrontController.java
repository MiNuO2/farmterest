package com.farmterest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.farmterest.controller.action.CartAddAction;
import com.farmterest.controller.action.CartRemoveAction;
import com.farmterest.controller.action.CartViewAction;
import com.farmterest.controller.action.JoinAction;
import com.farmterest.controller.action.LoginAction;
import com.farmterest.controller.action.LogoutAction;
import com.farmterest.controller.action.MainAction;
import com.farmterest.controller.action.MyPageAction;
import com.farmterest.controller.action.OrderAction;
import com.farmterest.controller.action.ProductDetailAction;
import com.farmterest.controller.action.ProductListAction;
import com.farmterest.controller.action.SearchAction;
import com.farmterest.controller.action.SellerProductDeleteAction;
import com.farmterest.controller.action.SellerProductFormAction;
import com.farmterest.controller.action.SellerProductSaveAction;
import com.farmterest.controller.action.SellerProductsAction;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 단일 진입점(Front Controller). 모든 *.do 요청을 받아
 * commandMap 으로 알맞은 Action 에 위임하고 결과 뷰로 분기한다.
 */
public class FrontController extends HttpServlet {

    private final Map<String, Action> commandMap = new HashMap<>();

    @Override
    public void init() {
        commandMap.put("/main.do", new MainAction());
        commandMap.put("/join.do", new JoinAction());
        commandMap.put("/login.do", new LoginAction());
        commandMap.put("/logout.do", new LogoutAction());
        commandMap.put("/productList.do", new ProductListAction());
        commandMap.put("/productDetail.do", new ProductDetailAction());
        commandMap.put("/search.do", new SearchAction());
        commandMap.put("/sellerProducts.do", new SellerProductsAction());
        commandMap.put("/sellerProductForm.do", new SellerProductFormAction());
        commandMap.put("/sellerProductSave.do", new SellerProductSaveAction());
        commandMap.put("/sellerProductDelete.do", new SellerProductDeleteAction());
        commandMap.put("/cart.do", new CartViewAction());
        commandMap.put("/cartAdd.do", new CartAddAction());
        commandMap.put("/cartRemove.do", new CartRemoveAction());
        commandMap.put("/order.do", new OrderAction());
        commandMap.put("/mypage.do", new MyPageAction());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String command = request.getRequestURI().substring(request.getContextPath().length());
        Action action = commandMap.get(command);
        if (action == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "알 수 없는 요청: " + command);
            return;
        }

        try {
            ActionForward forward = action.execute(request, response);
            if (forward == null) {
                return; // 액션이 직접 응답 처리
            }
            if (forward.isRedirect()) {
                response.sendRedirect(forward.getPath());
            } else {
                RequestDispatcher rd = request.getRequestDispatcher(forward.getPath());
                rd.forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}

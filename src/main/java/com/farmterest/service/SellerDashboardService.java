package com.farmterest.service;

import java.util.List;

import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.SellerStats;

/** 판매자 상품 목록(집계 포함)으로 대시보드 요약 통계를 만든다. */
public class SellerDashboardService {

    private static final int LOW_STOCK = 10;  // 재고 부족 기준

    /** 집계가 채워진 상품 리스트를 요약. */
    public SellerStats summarize(List<ProductDTO> products) {
        SellerStats s = new SellerStats();
        if (products == null || products.isEmpty()) {
            return s;
        }

        long totalStock = 0, totalSold = 0, monthSold = 0, totalRevenue = 0;
        int lowStock = 0, reviewCount = 0;
        double ratingWeightedSum = 0;
        ProductDTO best = null;

        for (ProductDTO p : products) {
            totalStock += p.getStock();
            totalSold += p.getTotalSold();
            monthSold += p.getMonthSold();
            totalRevenue += p.getRevenue();
            if (p.getStock() <= LOW_STOCK) {
                lowStock++;
            }
            if (p.getReviewCount() > 0 && p.getAvgRating() != null) {
                reviewCount += p.getReviewCount();
                ratingWeightedSum += p.getAvgRating() * p.getReviewCount();
            }
            if (p.getTotalSold() > 0 && (best == null || p.getTotalSold() > best.getTotalSold())) {
                best = p;
            }
        }

        s.setTotalProducts(products.size());
        s.setTotalStock(totalStock);
        s.setLowStockCount(lowStock);
        s.setTotalSold(totalSold);
        s.setMonthSold(monthSold);
        s.setTotalRevenue(totalRevenue);
        s.setReviewCount(reviewCount);
        s.setAvgRating(reviewCount > 0 ? Math.round((ratingWeightedSum / reviewCount) * 10.0) / 10.0 : 0);
        s.setBestSeller(best);
        return s;
    }
}

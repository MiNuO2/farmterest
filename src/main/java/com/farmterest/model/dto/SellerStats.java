package com.farmterest.model.dto;

/** 판매자 대시보드 요약 통계. */
public class SellerStats {

    private int totalProducts;     // 등록 상품 수
    private long totalStock;       // 총 재고 수량
    private int lowStockCount;     // 재고 부족(품절 임박) 상품 수
    private long totalSold;        // 누적 판매 수량
    private long monthSold;        // 이달 판매 수량
    private long totalRevenue;     // 누적 매출
    private int reviewCount;       // 받은 후기 수
    private double avgRating;      // 평균 평점(후기 가중)
    private ProductDTO bestSeller; // 베스트셀러(판매량 1위, 없으면 null)

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public long getTotalStock() { return totalStock; }
    public void setTotalStock(long totalStock) { this.totalStock = totalStock; }

    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getTotalSold() { return totalSold; }
    public void setTotalSold(long totalSold) { this.totalSold = totalSold; }

    public long getMonthSold() { return monthSold; }
    public void setMonthSold(long monthSold) { this.monthSold = monthSold; }

    public long getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    /** 별 채움 개수(0~5, 반올림). */
    public int getRatingStars() { return (int) Math.round(avgRating); }

    public ProductDTO getBestSeller() { return bestSeller; }
    public void setBestSeller(ProductDTO bestSeller) { this.bestSeller = bestSeller; }
}

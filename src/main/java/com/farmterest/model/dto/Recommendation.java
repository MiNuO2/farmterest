package com.farmterest.model.dto;

/** 추천 결과 = 상품 + 점수 + 추천 근거(설명). */
public class Recommendation {

    private ProductDTO product;
    private double score;
    private String reason;   // 추천 근거 문구 (설명가능성)

    public Recommendation() {
    }

    public Recommendation(ProductDTO product, double score, String reason) {
        this.product = product;
        this.score = score;
        this.reason = reason;
    }

    /** 화면 표시용 반올림 점수(0~100). */
    public int getScorePercent() {
        return (int) Math.round(score);
    }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

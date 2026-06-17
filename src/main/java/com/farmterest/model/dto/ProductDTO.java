package com.farmterest.model.dto;

import java.sql.Timestamp;

/**
 * 상품 자바빈. 품질지표(정백도/완전립/수분/식미치)는 품목에 따라 없을 수 있어
 * 래퍼 타입(null 허용)으로 둔다.
 */
public class ProductDTO {

    private int productId;
    private int sellerId;
    private String sellerName;      // member 조인 표시용
    private String name;
    private String category;
    private String region;
    private int price;
    private int stock;
    private String imageUrl;
    private String description;
    private Integer polishedRate;    // 정백도(%)
    private Integer wholeGrainRate;  // 완전립 비율(%)
    private Double moisture;         // 수분함량(%)
    private Integer tasteScore;      // 식미치(점)
    private Timestamp createdAt;

    public ProductDTO() {
    }

    /** 품질지표가 하나라도 있으면 true (지표 배지 노출용). */
    public boolean hasQuality() {
        return polishedRate != null || wholeGrainRate != null
                || moisture != null || tasteScore != null;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPolishedRate() { return polishedRate; }
    public void setPolishedRate(Integer polishedRate) { this.polishedRate = polishedRate; }

    public Integer getWholeGrainRate() { return wholeGrainRate; }
    public void setWholeGrainRate(Integer wholeGrainRate) { this.wholeGrainRate = wholeGrainRate; }

    public Double getMoisture() { return moisture; }
    public void setMoisture(Double moisture) { this.moisture = moisture; }

    public Integer getTasteScore() { return tasteScore; }
    public void setTasteScore(Integer tasteScore) { this.tasteScore = tasteScore; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

package com.farmterest.model.dto;

import java.sql.Timestamp;

/** 상품 후기(별점+코멘트) 자바빈. */
public class ReviewDTO {

    private int reviewId;
    private int productId;
    private int memberId;
    private Integer orderItemId;     // 어떤 주문항목에 대한 후기인지(없을 수 있음)
    private int rating;              // 1~5
    private String comment;
    private Timestamp createdAt;

    private String memberName;       // member 조인 표시용
    private String productName;      // product 조인 표시용

    public ReviewDTO() {
    }

    /** 별 채움 개수(별점 위젯용). */
    public int getStars() {
        return rating;
    }

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}

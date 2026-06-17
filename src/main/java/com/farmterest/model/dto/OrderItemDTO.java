package com.farmterest.model.dto;

/** 주문 상세 항목 자바빈. */
public class OrderItemDTO {

    private int orderItemId;
    private int orderId;
    private int productId;
    private String productName;   // product 조인 표시용
    private int qty;
    private int unitPrice;

    public OrderItemDTO() {
    }

    public int getSubtotal() {
        return qty * unitPrice;
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
}

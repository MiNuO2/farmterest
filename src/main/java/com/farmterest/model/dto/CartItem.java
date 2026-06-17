package com.farmterest.model.dto;

/** 세션 장바구니 항목. */
public class CartItem {

    private int productId;
    private String productName;
    private String category;
    private int price;
    private int qty;

    public CartItem() {
    }

    public CartItem(int productId, String productName, String category, int price, int qty) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.qty = qty;
    }

    public int getSubtotal() {
        return price * qty;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}

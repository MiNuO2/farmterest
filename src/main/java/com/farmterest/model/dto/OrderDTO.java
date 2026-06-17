package com.farmterest.model.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** 주문 자바빈 (주문 상세 목록 포함). */
public class OrderDTO {

    private int orderId;
    private int memberId;
    private Timestamp orderedAt;
    private int totalPrice;
    private String status;
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO() {
    }

    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public Timestamp getOrderedAt() { return orderedAt; }
    public void setOrderedAt(Timestamp orderedAt) { this.orderedAt = orderedAt; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}

package com.amap.marketplace.model;

import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private String producerId;
    private String name;
    private Double price;
    private Integer quantity;

    public Double getSubtotal() {
        return price * quantity;
    }
}
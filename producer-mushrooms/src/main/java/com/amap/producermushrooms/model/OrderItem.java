package com.amap.producermushrooms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class OrderItem {
    private String productId;
    private String name;
    private Integer quantity;
    private Double unitPrice;
}
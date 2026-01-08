package com.amap.producerbread.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;

    @Field("producer_id")
    private String producerId;

    @Field("client_id")
    private String clientId;

    private LocalDateTime date;
    private String status;
    private List<OrderItem> products;
    private Double total;
}
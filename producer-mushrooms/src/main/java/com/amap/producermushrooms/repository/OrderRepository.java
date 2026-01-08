package com.amap.producermushrooms.repository;

import com.amap.producermushrooms.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByProducerId(String producerId);
}
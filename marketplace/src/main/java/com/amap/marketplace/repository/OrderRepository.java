package com.amap.marketplace.repository;

import com.amap.marketplace.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByClientIdOrderByDateDesc(String clientId);
}
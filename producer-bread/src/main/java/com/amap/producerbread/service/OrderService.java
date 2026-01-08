package com.amap.producerbread.service;

import com.amap.producerbread.model.Order;
import com.amap.producerbread.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> getAllOrdersByProducer(String producerId) {
        return orderRepository.findByProducerId(producerId);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }
}
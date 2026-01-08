package com.amap.producermushrooms.service;

import com.amap.producermushrooms.model.Order;
import com.amap.producermushrooms.repository.OrderRepository;
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
package com.amap.producer.service;

import com.amap.producer.model.Order;
import com.amap.producer.model.OrderStatus;
import com.amap.producer.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public List<Order> getAllOrdersByProducer(String producerId) {
        return orderRepository.findByProducerId(producerId);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order acceptOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            // Vérifier que la commande est bien en PENDING
            if (order.getStatus() != OrderStatus.PENDING) {
                return order; // Ne rien faire si déjà traité
            }

            // Déduire le stock pour chaque produit
            order.getProducts().forEach(item -> {
                productService.updateStock(item.getProductId(), -item.getQuantity());
            });

            order.setStatus(OrderStatus.ACCEPTED);
            return orderRepository.save(order);
        }
        return null;
    }

    @Transactional
    public Order rejectOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            // Vérifier que la commande est bien en PENDING
            if (order.getStatus() != OrderStatus.PENDING) {
                return order; // Ne rien faire si déjà traité
            }

            // Si on rejette, on ne touche PAS au stock (il n'a jamais été déduit)
            order.setStatus(OrderStatus.REJECTED);
            return orderRepository.save(order);
        }
        return null;
    }
}
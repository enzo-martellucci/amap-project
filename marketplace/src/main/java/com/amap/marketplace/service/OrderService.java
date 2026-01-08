package com.amap.marketplace.service;

import com.amap.marketplace.model.Cart;
import com.amap.marketplace.model.Order;
import com.amap.marketplace.model.OrderItem;
import com.amap.marketplace.model.OrderStatus;
import com.amap.marketplace.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void createOrdersFromCart(String clientId, Cart cart) {
        // Group cart items by producer
        Map<String, List<com.amap.marketplace.model.CartItem>> itemsByProducer =
                cart.getItems().stream()
                        .collect(Collectors.groupingBy(com.amap.marketplace.model.CartItem::getProducerId));

        // Create one order per producer
        for (Map.Entry<String, List<com.amap.marketplace.model.CartItem>> entry : itemsByProducer.entrySet()) {
            String producerId = entry.getKey();
            List<com.amap.marketplace.model.CartItem> items = entry.getValue();

            Order order = new Order();
            order.setProducerId(producerId);
            order.setClientId(clientId);
            order.setDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDING);  // Créé avec statut PENDING

            List<OrderItem> orderItems = new ArrayList<>();
            double total = 0.0;

            for (com.amap.marketplace.model.CartItem cartItem : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setName(cartItem.getName());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getPrice());
                orderItems.add(orderItem);

                total += cartItem.getSubtotal();
            }

            order.setProducts(orderItems);
            order.setTotal(total);

            orderRepository.save(order);
        }
    }

    public List<Order> getOrdersByClient(String clientId) {
        return orderRepository.findByClientIdOrderByDateDesc(clientId);
    }
}
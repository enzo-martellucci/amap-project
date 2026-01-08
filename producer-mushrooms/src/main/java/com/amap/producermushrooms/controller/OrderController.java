package com.amap.producermushrooms.controller;

import com.amap.producermushrooms.config.ProducerConfig;
import com.amap.producermushrooms.model.Order;
import com.amap.producermushrooms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ProducerConfig producerConfig;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrdersByProducer(producerConfig.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("producerName", producerConfig.getName());
        return "orders";
    }
}
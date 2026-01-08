package com.amap.marketplace.controller;

import com.amap.marketplace.model.Order;
import com.amap.marketplace.service.OrderService;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping
    public String viewOrders(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByClient(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "orders";
    }
}
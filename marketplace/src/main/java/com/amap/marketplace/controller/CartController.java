package com.amap.marketplace.controller;

import com.amap.marketplace.model.Cart;
import com.amap.marketplace.model.Product;
import com.amap.marketplace.service.OrderService;
import com.amap.marketplace.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        Product product = productService.getProductById(productId).orElse(null);
        if (product != null) {
            cart.addItem(product, quantity);
            session.setAttribute("cart", cart);
        }

        return "redirect:/catalog";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String productId, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(productId);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null && !cart.getItems().isEmpty()) {
            orderService.createOrdersFromCart(userId, cart);
            cart.clear();
            session.setAttribute("cart", cart);
        }

        return "redirect:/orders";
    }
}
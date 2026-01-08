package com.amap.marketplace.controller;

import com.amap.marketplace.model.Product;
import com.amap.marketplace.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public String catalog(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Product> products = productService.getAllAvailableProducts();
        model.addAttribute("products", products);
        model.addAttribute("userName", session.getAttribute("userName"));

        return "catalog";
    }
}
package com.amap.producer.controller;

import com.amap.producer.config.ProducerConfig;
import com.amap.producer.model.Product;
import com.amap.producer.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProducerConfig producerConfig;

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProductsByProducer(producerConfig.getId());
        model.addAttribute("products", products);
        model.addAttribute("producerName", producerConfig.getName());
        return "products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("producerName", producerConfig.getName());
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product) {
        product.setProducerId(producerConfig.getId());
        product.setAvailable(true);
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
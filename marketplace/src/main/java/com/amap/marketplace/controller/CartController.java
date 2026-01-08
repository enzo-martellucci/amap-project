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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
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
            // Vérifier le stock disponible
            int currentQuantityInCart = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .map(item -> item.getQuantity())
                    .orElse(0);

            int totalQuantity = currentQuantityInCart + quantity;

            if (totalQuantity > product.getStock()) {
                redirectAttributes.addFlashAttribute("error",
                        "Not enough stock! Only " + product.getStock() + " available.");
                return "redirect:/catalog";
            }

            cart.addItem(product, quantity);
            session.setAttribute("cart", cart);
            redirectAttributes.addFlashAttribute("success",
                    "Product added to cart!");
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
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null && !cart.getItems().isEmpty()) {
            // Vérifier les stocks avant de créer les commandes
            for (var item : cart.getItems()) {
                Product product = productService.getProductById(item.getProductId()).orElse(null);
                if (product == null || product.getStock() < item.getQuantity()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Some products are out of stock. Please update your cart.");
                    return "redirect:/cart";
                }
            }

            orderService.createOrdersFromCart(userId, cart);
            cart.clear();
            session.setAttribute("cart", cart);
            redirectAttributes.addFlashAttribute("success",
                    "Order placed successfully!");
        }

        return "redirect:/orders";
    }
}
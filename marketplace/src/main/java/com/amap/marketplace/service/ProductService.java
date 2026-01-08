package com.amap.marketplace.service;

import com.amap.marketplace.model.Product;
import com.amap.marketplace.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
}
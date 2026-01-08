package com.amap.producermushrooms.service;

import com.amap.producermushrooms.model.Product;
import com.amap.producermushrooms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProductsByProducer(String producerId) {
        return productRepository.findByProducerId(producerId);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    // Nouvelle méthode pour mettre à jour le stock
    public void updateStock(String productId, int quantityChange) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            int newStock = product.getStock() + quantityChange;
            product.setStock(Math.max(0, newStock)); // Ne jamais avoir de stock négatif
            productRepository.save(product);
        }
    }
}
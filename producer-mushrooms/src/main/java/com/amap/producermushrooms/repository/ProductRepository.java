package com.amap.producermushrooms.repository;

import com.amap.producermushrooms.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByProducerId(String producerId);
}
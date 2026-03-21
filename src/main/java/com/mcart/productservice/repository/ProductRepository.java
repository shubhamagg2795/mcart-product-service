package com.mcart.productservice.repository;

import com.mcart.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // MongoDB fallback search when OpenSearch is disabled
    List<Product> findByNameContainingIgnoreCase(String name);
}

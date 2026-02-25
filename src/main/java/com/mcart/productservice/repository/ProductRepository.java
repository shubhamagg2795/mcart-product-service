package com.mcart.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.mcart.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
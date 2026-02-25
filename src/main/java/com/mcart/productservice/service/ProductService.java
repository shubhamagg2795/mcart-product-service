package com.mcart.productservice.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.mcart.productservice.model.Product;
import com.mcart.productservice.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public List<Product> getAll() {
        return repository.findAll();
    }
}
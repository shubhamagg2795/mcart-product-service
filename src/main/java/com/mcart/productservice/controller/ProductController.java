package com.mcart.productservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.mcart.productservice.model.Product;
import com.mcart.productservice.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return service.save(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return service.getAll();
    }
}
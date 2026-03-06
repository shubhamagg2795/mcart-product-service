package com.mcart.productservice.controller;

import com.mcart.productservice.model.Product;
import com.mcart.productservice.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")  // temporary
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
    public List<Product> getProducts(@RequestParam(required = false) String q) {

        if (q != null && !q.isEmpty()) {
            return service.searchByName(q);
        }

        return service.getAll();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        service.delete(id);
    }
}
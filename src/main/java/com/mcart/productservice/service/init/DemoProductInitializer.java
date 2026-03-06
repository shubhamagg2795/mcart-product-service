/*
package com.mcart.productservice.service.init;
import com.mcart.productservice.model.Product;
import com.mcart.productservice.service.ProductService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

//@Component
public class DemoProductInitializer {

    private final ProductService productService;

    public DemoProductInitializer(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void createDemoProducts() {

        // delete all existing products
        productService.deleteAll();

        String[] categories = {"Laptop","Smartphone","Headphones","Smartwatch","Camera","Tablet","Speaker","Monitor","Keyboard","Mouse"};
        double basePrice = 100;

        for (int i = 1; i <= 60; i++) {
            String category = categories[i % categories.length];
            String name = category + " " + i;
            String description = "High-quality " + category + " number " + i;
            double price = basePrice + (i * 10);

            productService.save(new Product(name, description, price));
        }

        System.out.println("Fresh 60 demo products created!");
    }
}*/

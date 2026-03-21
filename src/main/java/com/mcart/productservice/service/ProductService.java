package com.mcart.productservice.service;

import com.mcart.productservice.model.Product;
import com.mcart.productservice.repository.ProductRepository;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    // Optional — only injected when OpenSearch is enabled
    // When disabled, this will be null and we fall back to MongoDB
    @Autowired(required = false)
    private RestHighLevelClient client;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        Product saved = repository.save(product);

        // Index in OpenSearch only if available
        if (client != null) {
            IndexRequest request = new IndexRequest("products")
                    .id(saved.getId())
                    .source(Map.of(
                            "name", saved.getName(),
                            "description", saved.getDescription(),
                            "price", saved.getPrice()
                    ));
            try {
                client.index(request, RequestOptions.DEFAULT);
            } catch (Exception e) {
                System.out.println("OpenSearch indexing skipped: " + e.getMessage());
            }
        }

        return saved;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> searchByName(String name) {

        // If OpenSearch is available — use it
        if (client != null) {
            return searchWithOpenSearch(name);
        }

        // Fallback — use MongoDB regex search
        System.out.println("OpenSearch not available — using MongoDB search");
        return repository.findByNameContainingIgnoreCase(name);
    }

    private List<Product> searchWithOpenSearch(String name) {
        List<Product> results = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("products");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", name));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            response.getHits().forEach(hit -> {
                Map<String, Object> map = hit.getSourceAsMap();
                Product p = new Product();
                p.setId(hit.getId());
                p.setName((String) map.get("name"));
                p.setDescription((String) map.get("description"));
                p.setPrice(Double.parseDouble(map.get("price").toString()));
                results.add(p);
            });
        } catch (Exception e) {
            System.out.println("OpenSearch search failed, falling back to MongoDB: " + e.getMessage());
            return repository.findByNameContainingIgnoreCase(name);
        }

        return results;
    }

    public void delete(String id) {
        repository.deleteById(id);

        if (client != null) {
            try {
                client.delete(
                        new org.opensearch.action.delete.DeleteRequest("products", id),
                        RequestOptions.DEFAULT
                );
            } catch (Exception e) {
                System.out.println("OpenSearch delete skipped: " + e.getMessage());
            }
        }
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}

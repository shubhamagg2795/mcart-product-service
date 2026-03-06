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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final RestHighLevelClient client;

    public ProductService(ProductRepository repository, RestHighLevelClient client) {
        this.repository = repository;
        this.client = client;
    }

    public Product save(Product product) {
        Product saved = repository.save(product);

        // index in OpenSearch
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
            e.printStackTrace();
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
        List<Product> results = new ArrayList<>();

        // query OpenSearch
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
            e.printStackTrace();
        }

        return results;
    }

    public void delete(String id) {
        repository.deleteById(id);

        try {
            client.delete(new org.opensearch.action.delete.DeleteRequest("products", id), RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteAll() {
        repository.deleteAll();
    }
}
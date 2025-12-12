package com.demo.ui.services;

import com.demo.ui.models.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class ProductService {
    private final ApiClient api = new ApiClient("http://localhost:3000");
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Product> getAllProducts() throws Exception {
        String body = api.get("/products");
        return Arrays.asList(mapper.readValue(body, Product[].class));
    }

    public Product getProduct(int id) throws Exception {
        String body = api.get("/products/" + id);
        return mapper.readValue(body, Product.class);
    }

    public Product createProduct(String name, String description, double price, int stock, Integer providerId) throws Exception {
        String json = mapper.writeValueAsString(new Product(0, name, description, price, stock, providerId, null, null));
        String body = api.post("/products", json);
        return mapper.readValue(body, Product.class);
    }

    public Product updateProduct(int id, String name, String description, double price, int stock, Integer providerId) throws Exception {
        String json = mapper.writeValueAsString(new Product(id, name, description, price, stock, providerId, null, null));
        String body = api.put("/products/" + id, json);
        return mapper.readValue(body, Product.class);
    }

    public void deleteProduct(int id) throws Exception {
        api.delete("/products/" + id);
    }
}

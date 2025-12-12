package com.demo.ui.services;

import com.demo.ui.models.OrderProduct;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class OrderProductService {
    private final ApiClient api = new ApiClient("http://localhost:3000");
    private final ObjectMapper mapper = new ObjectMapper();

    // Get all products for a given order
    public List<OrderProduct> getProductsForOrder(int orderId) throws Exception {
        String body = api.get("/orders/" + orderId + "/products");
        return Arrays.asList(mapper.readValue(body, OrderProduct[].class));
    }

    // Add a product to an order
    public OrderProduct addProductToOrder(int orderId, int productId, int quantity, double unitPrice) throws Exception {
        OrderProduct newItem = new OrderProduct(0, orderId, productId, quantity, unitPrice, 0.0);
        String json = mapper.writeValueAsString(newItem);
        String body = api.post("/orders/" + orderId + "/products", json);
        return mapper.readValue(body, OrderProduct.class);
    }

    // Update a product in an order
    public OrderProduct updateProductInOrder(int orderId, int itemId, int productId, int quantity, double unitPrice) throws Exception {
        OrderProduct updatedItem = new OrderProduct(itemId, orderId, productId, quantity, unitPrice, 0.0);
        String json = mapper.writeValueAsString(updatedItem);
        String body = api.put("/orders/" + orderId + "/products/" + itemId, json);
        return mapper.readValue(body, OrderProduct.class);
    }

    // Remove a product from an order
    public void removeProductFromOrder(int orderId, int itemId) throws Exception {
        api.delete("/orders/" + orderId + "/products/" + itemId);
    }
}

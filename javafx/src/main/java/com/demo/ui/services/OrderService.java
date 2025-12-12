package com.demo.ui.services;

import com.demo.ui.models.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class OrderService {
    private final ApiClient api = new ApiClient("http://localhost:3000");
    private final ObjectMapper mapper = new ObjectMapper();

    // Get all orders
    public List<Order> getAllOrders() throws Exception {
        String body = api.get("/orders");
        return Arrays.asList(mapper.readValue(body, Order[].class));
    }

    // Get a single order by ID
    public Order getOrder(int id) throws Exception {
        String body = api.get("/orders/" + id);
        return mapper.readValue(body, Order.class);
    }

    // Create a new order
    public Order createOrder(String orderNumber, int clientId, String orderStatus,
                             String paymentMethod, String currencyCode,
                             String shippingAddress, String billingAddress, String notes) throws Exception {
        // id=0, amounts default to 0 → backend triggers will recalc totals
        Order newOrder = new Order(
            0, orderNumber, clientId, null, orderStatus, paymentMethod, currencyCode,
            0.0, 0.0, 0.0, 0.0,
            shippingAddress, billingAddress, notes,
            null, null
        );
        String json = mapper.writeValueAsString(newOrder);
        String body = api.post("/orders", json);
        return mapper.readValue(body, Order.class);
    }

    // Update an existing order
    public Order updateOrder(int id, String orderNumber, int clientId, String orderStatus,
                             String paymentMethod, String currencyCode,
                             String shippingAddress, String billingAddress, String notes) throws Exception {
        Order updatedOrder = new Order(
            id, orderNumber, clientId, null, orderStatus, paymentMethod, currencyCode,
            0.0, 0.0, 0.0, 0.0,
            shippingAddress, billingAddress, notes,
            null, null
        );
        String json = mapper.writeValueAsString(updatedOrder);
        String body = api.put("/orders/" + id, json);
        return mapper.readValue(body, Order.class);
    }

    // Delete an order
    public void deleteOrder(int id) throws Exception {
        api.delete("/orders/" + id);
    }
}

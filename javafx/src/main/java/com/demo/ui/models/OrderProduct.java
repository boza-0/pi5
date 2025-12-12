package com.demo.ui.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderProduct {
    private int id;

    @JsonProperty("order_id")
    private int orderId;

    @JsonProperty("product_id")
    private int productId;

    private int quantity;

    @JsonProperty("unit_price")
    private double unitPrice;

    @JsonProperty("line_total")
    private double lineTotal;

    public OrderProduct() { }

    public OrderProduct(int id, int orderId, int productId,
                        int quantity, double unitPrice, double lineTotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
}

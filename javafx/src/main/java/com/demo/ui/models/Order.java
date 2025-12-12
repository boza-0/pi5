package com.demo.ui.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    private int id;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("client_id")
    private int clientId;

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("subtotal_amount")
    private double subtotalAmount;

    @JsonProperty("discount_amount")
    private double discountAmount;

    @JsonProperty("tax_amount")
    private double taxAmount;

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("billing_address")
    private String billingAddress;

    private String notes;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public Order() { }

    public Order(int id, String orderNumber, int clientId, String orderDate,
                 String orderStatus, String paymentMethod, String currencyCode,
                 double subtotalAmount, double discountAmount, double taxAmount, double totalAmount,
                 String shippingAddress, String billingAddress, String notes,
                 String createdAt, String updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.clientId = clientId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.currencyCode = currencyCode;
        this.subtotalAmount = subtotalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public double getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(double subtotalAmount) { this.subtotalAmount = subtotalAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

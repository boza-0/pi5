package com.demo.ui.viewmodels;

import com.demo.ui.models.Order;
import com.demo.ui.services.OrderService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class OrdersViewModel {

    private final OrderService service = new OrderService();

    // Table data
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    // Selection
    private final ObjectProperty<Order> selectedOrder = new SimpleObjectProperty<>(null);
    private final BooleanProperty hasSelection = new SimpleBooleanProperty(false);

    // Form fields
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final IntegerProperty clientId = new SimpleIntegerProperty(0);
    private final StringProperty orderDate = new SimpleStringProperty("");
    private final StringProperty orderStatus = new SimpleStringProperty("pending");
    private final StringProperty paymentMethod = new SimpleStringProperty("credit_card");
    private final StringProperty currencyCode = new SimpleStringProperty("EUR");

    private final DoubleProperty subtotalAmount = new SimpleDoubleProperty(0.0);
    private final DoubleProperty discountAmount = new SimpleDoubleProperty(0.0);
    private final DoubleProperty taxAmount = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalAmount = new SimpleDoubleProperty(0.0);

    private final StringProperty shippingAddress = new SimpleStringProperty("");
    private final StringProperty billingAddress = new SimpleStringProperty("");
    private final StringProperty notes = new SimpleStringProperty("");

    private final StringProperty createdAt = new SimpleStringProperty("");
    private final StringProperty updatedAt = new SimpleStringProperty("");

    // Status message
    private final StringProperty status = new SimpleStringProperty("");

    public OrdersViewModel() {
        // When selection changes, populate form fields
        selectedOrder.addListener((obs, old, sel) -> {
            hasSelection.set(sel != null);
            if (sel != null) {
                orderNumber.set(sel.getOrderNumber() != null ? sel.getOrderNumber() : "");
                clientId.set(sel.getClientId());
                orderDate.set(sel.getOrderDate() != null ? sel.getOrderDate() : "");
                orderStatus.set(sel.getOrderStatus() != null ? sel.getOrderStatus() : "");
                paymentMethod.set(sel.getPaymentMethod() != null ? sel.getPaymentMethod() : "");
                currencyCode.set(sel.getCurrencyCode() != null ? sel.getCurrencyCode() : "");
                subtotalAmount.set(sel.getSubtotalAmount());
                discountAmount.set(sel.getDiscountAmount());
                taxAmount.set(sel.getTaxAmount());
                totalAmount.set(sel.getTotalAmount());
                shippingAddress.set(sel.getShippingAddress() != null ? sel.getShippingAddress() : "");
                billingAddress.set(sel.getBillingAddress() != null ? sel.getBillingAddress() : "");
                notes.set(sel.getNotes() != null ? sel.getNotes() : "");
                createdAt.set(sel.getCreatedAt() != null ? sel.getCreatedAt() : "");
                updatedAt.set(sel.getUpdatedAt() != null ? sel.getUpdatedAt() : "");
            } else {
                clearForm();
            }
        });
    }

    // Async operations
    public void loadOrdersAsync() {
        status.set("Loading orders...");
        new Thread(() -> {
            try {
                List<Order> list = service.getAllOrders();
                Platform.runLater(() -> {
                    orders.setAll(list);
                    status.set("Loaded " + list.size() + " orders");
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Load failed: " + e.getMessage()));
            }
        }, "load-orders").start();
    }

    public void createOrderAsync() {
        String num = orderNumber.get().trim();
        int cid = clientId.get();
        String stat = orderStatus.get().trim();
        String pay = paymentMethod.get().trim();
        String curr = currencyCode.get().trim();
        String ship = shippingAddress.get().trim();
        String bill = billingAddress.get().trim();
        String nts = notes.get().trim();

        if (num.isBlank()) {
            status.set("Order number is required");
            return;
        }
        if (cid <= 0) {
            status.set("Valid client ID is required");
            return;
        }

        status.set("Creating order...");
        new Thread(() -> {
            try {
                Order created = service.createOrder(num, cid, stat, pay, curr, ship, bill, nts);
                Platform.runLater(() -> {
                    orders.add(0, created);
                    setSelectedOrder(created);
                    status.set("Created order ID " + created.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Create failed: " + e.getMessage()));
            }
        }, "create-order").start();
    }

    public void updateSelectedOrderAsync() {
        Order sel = selectedOrder.get();
        if (sel == null) {
            status.set("No order selected");
            return;
        }

        String num = orderNumber.get().trim();
        int cid = clientId.get();
        String stat = orderStatus.get().trim();
        String pay = paymentMethod.get().trim();
        String curr = currencyCode.get().trim();
        String ship = shippingAddress.get().trim();
        String bill = billingAddress.get().trim();
        String nts = notes.get().trim();

        if (num.isBlank()) {
            status.set("Order number is required");
            return;
        }
        if (cid <= 0) {
            status.set("Valid client ID is required");
            return;
        }

        final int id = sel.getId();
        status.set("Updating order...");
        new Thread(() -> {
            try {
                Order updated = service.updateOrder(id, num, cid, stat, pay, curr, ship, bill, nts);
                Platform.runLater(() -> {
                    int idx = orders.indexOf(sel);
                    if (idx >= 0) orders.set(idx, updated);
                    setSelectedOrder(updated);
                    status.set("Updated order ID " + updated.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Update failed: " + e.getMessage()));
            }
        }, "update-order").start();
    }

    public void deleteSelectedOrderAsync() {
        Order sel = selectedOrder.get();
        if (sel == null) {
            status.set("No order selected");
            return;
        }
        final int id = sel.getId();
        status.set("Deleting order...");
        new Thread(() -> {
            try {
                service.deleteOrder(id);
                Platform.runLater(() -> {
                    orders.remove(sel);
                    setSelectedOrder(null);
                    status.set("Deleted order ID " + id);
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Delete failed: " + e.getMessage()));
            }
        }, "delete-order").start();
    }

    private void clearForm() {
        orderNumber.set("");
        clientId.set(0);
        orderDate.set("");
        orderStatus.set("pending");
        paymentMethod.set("credit_card");
        currencyCode.set("EUR");
        subtotalAmount.set(0.0);
        discountAmount.set(0.0);
        taxAmount.set(0.0);
        totalAmount.set(0.0);
        shippingAddress.set("");
        billingAddress.set("");
        notes.set("");
        createdAt.set("");
        updatedAt.set("");
    }

    // Exposed properties for binding
    public ObservableList<Order> getOrders() { return orders; }
    public ObjectProperty<Order> selectedOrderProperty() { return selectedOrder; }
    public void setSelectedOrder(Order o) { selectedOrder.set(o); }
    public BooleanProperty hasSelectionProperty() { return hasSelection; }

    public StringProperty orderNumberProperty() { return orderNumber; }
    public IntegerProperty clientIdProperty() { return clientId; }
    public StringProperty orderDateProperty() { return orderDate; }
    public StringProperty orderStatusProperty() { return orderStatus; }
    public StringProperty paymentMethodProperty() { return paymentMethod; }
    public StringProperty currencyCodeProperty() { return currencyCode; }

    public DoubleProperty subtotalAmountProperty() { return subtotalAmount; }
    public DoubleProperty discountAmountProperty() { return discountAmount; }
    public DoubleProperty taxAmountProperty() { return taxAmount; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }

    public StringProperty shippingAddressProperty() { return shippingAddress; }
    public StringProperty billingAddressProperty() { return billingAddress; }
    public StringProperty notesProperty() { return notes; }

    public StringProperty createdAtProperty() { return createdAt; }
    public StringProperty updatedAtProperty() { return updatedAt; }

    public StringProperty statusProperty() { return status; }
}

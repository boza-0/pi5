package com.demo.ui.viewmodels;

import com.demo.ui.models.OrderProduct;
import com.demo.ui.services.OrderProductService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class OrderProductsViewModel {

    private final OrderProductService service = new OrderProductService();

    // Table data
    private final ObservableList<OrderProduct> orderProducts = FXCollections.observableArrayList();

    // Selection
    private final ObjectProperty<OrderProduct> selectedItem = new SimpleObjectProperty<>(null);
    private final BooleanProperty hasSelection = new SimpleBooleanProperty(false);

    // Form fields
    private final IntegerProperty orderId = new SimpleIntegerProperty(0);
    private final IntegerProperty productId = new SimpleIntegerProperty(0);
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final DoubleProperty unitPrice = new SimpleDoubleProperty(0.0);
    private final DoubleProperty lineTotal = new SimpleDoubleProperty(0.0);

    private final StringProperty status = new SimpleStringProperty("");

    public OrderProductsViewModel() {
        // When selection changes, populate form fields
        selectedItem.addListener((obs, old, sel) -> {
            hasSelection.set(sel != null);
            if (sel != null) {
                orderId.set(sel.getOrderId());
                productId.set(sel.getProductId());
                quantity.set(sel.getQuantity());
                unitPrice.set(sel.getUnitPrice());
                lineTotal.set(sel.getLineTotal());
            } else {
                clearForm();
            }
        });
    }

    // Async operations
    public void loadProductsForOrderAsync(int oid) {
        status.set("Loading products for order " + oid + "...");
        new Thread(() -> {
            try {
                List<OrderProduct> list = service.getProductsForOrder(oid);
                Platform.runLater(() -> {
                    orderProducts.setAll(list);
                    status.set("Loaded " + list.size() + " items");
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Load failed: " + e.getMessage()));
            }
        }, "load-order-products").start();
    }

    public void addProductAsync(int oid) {
        int pid = productId.get();
        int qty = quantity.get();
        double price = unitPrice.get();

        if (pid <= 0) {
            status.set("Valid product ID required");
            return;
        }
        if (qty <= 0) {
            status.set("Quantity must be positive");
            return;
        }

        status.set("Adding product to order...");
        new Thread(() -> {
            try {
                OrderProduct created = service.addProductToOrder(oid, pid, qty, price);
                Platform.runLater(() -> {
                    orderProducts.add(created);
                    setSelectedItem(created);
                    status.set("Added product ID " + created.getProductId() + " to order " + oid);
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Add failed: " + e.getMessage()));
            }
        }, "add-order-product").start();
    }

    public void updateSelectedItemAsync(int oid) {
        OrderProduct sel = selectedItem.get();
        if (sel == null) {
            status.set("No item selected");
            return;
        }

        int pid = productId.get();
        int qty = quantity.get();
        double price = unitPrice.get();

        if (pid <= 0) {
            status.set("Valid product ID required");
            return;
        }
        if (qty <= 0) {
            status.set("Quantity must be positive");
            return;
        }

        final int itemId = sel.getId();
        status.set("Updating product in order...");
        new Thread(() -> {
            try {
                OrderProduct updated = service.updateProductInOrder(oid, itemId, pid, qty, price);
                Platform.runLater(() -> {
                    int idx = orderProducts.indexOf(sel);
                    if (idx >= 0) orderProducts.set(idx, updated);
                    setSelectedItem(updated);
                    status.set("Updated item ID " + updated.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Update failed: " + e.getMessage()));
            }
        }, "update-order-product").start();
    }

    public void removeSelectedItemAsync(int oid) {
        OrderProduct sel = selectedItem.get();
        if (sel == null) {
            status.set("No item selected");
            return;
        }
        final int itemId = sel.getId();
        status.set("Removing product from order...");
        new Thread(() -> {
            try {
                service.removeProductFromOrder(oid, itemId);
                Platform.runLater(() -> {
                    orderProducts.remove(sel);
                    setSelectedItem(null);
                    status.set("Removed item ID " + itemId);
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Remove failed: " + e.getMessage()));
            }
        }, "remove-order-product").start();
    }

    private void clearForm() {
        orderId.set(0);
        productId.set(0);
        quantity.set(1);
        unitPrice.set(0.0);
        lineTotal.set(0.0);
    }

    // Exposed properties for binding
    public ObservableList<OrderProduct> getOrderProducts() { return orderProducts; }
    public ObjectProperty<OrderProduct> selectedItemProperty() { return selectedItem; }
    public void setSelectedItem(OrderProduct op) { selectedItem.set(op); }
    public BooleanProperty hasSelectionProperty() { return hasSelection; }

    public IntegerProperty orderIdProperty() { return orderId; }
    public IntegerProperty productIdProperty() { return productId; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty lineTotalProperty() { return lineTotal; }

    public StringProperty statusProperty() { return status; }
}

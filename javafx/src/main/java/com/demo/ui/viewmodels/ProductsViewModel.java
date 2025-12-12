package com.demo.ui.viewmodels;

import com.demo.ui.models.Product;
import com.demo.ui.services.ProductService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ProductsViewModel {

    private final ProductService service = new ProductService();

    // Table data
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    // Selection
    private final ObjectProperty<Product> selectedProduct = new SimpleObjectProperty<>(null);
    private final BooleanProperty hasSelection = new SimpleBooleanProperty(false);

    // Form fields (as plain text for easy binding/validation)
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty priceText = new SimpleStringProperty("");
    private final StringProperty stockText = new SimpleStringProperty("");//1
    private final StringProperty providerIdText = new SimpleStringProperty("");
    private final StringProperty createdAt = new SimpleStringProperty("");
    private final StringProperty updatedAt = new SimpleStringProperty("");

    // Status message
    private final StringProperty status = new SimpleStringProperty("");

    public ProductsViewModel() {
        // When selection changes, populate form fields
        selectedProduct.addListener((obs, old, sel) -> {
            hasSelection.set(sel != null);
            if (sel != null) {
                name.set(sel.getName() != null ? sel.getName() : "");
                description.set(sel.getDescription() != null ? sel.getDescription() : "");
                priceText.set(String.valueOf(sel.getPrice()));
                stockText.set(String.valueOf(sel.getStock()));//3
                providerIdText.set(sel.getProviderId() != null ? String.valueOf(sel.getProviderId()) : "");
                createdAt.set(sel.getCreatedAt() != null ? sel.getCreatedAt() : "");
                updatedAt.set(sel.getUpdatedAt() != null ? sel.getUpdatedAt() : "");
            } else {
                clearForm();
            }
        });

        // Simple validation feedback: price must be numeric and >= 0
        priceText.addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                status.set("Price is required");
                return;
            }
            try {
                double p = Double.parseDouble(n);
                if (p < 0) status.set("Price must be >= 0");
                else status.set("");
            } catch (NumberFormatException e) {
                status.set("Price must be numeric");
            }
        });

        stockText.addListener((obs, o, n) -> {//5 (entire block)
            if (n == null || n.isBlank()) {
                status.set("Stock is required");
                return;
            }
            try {
                int s = Integer.parseInt(n);
                if (s < 0) status.set("Stock must be >= 0");
                else status.set("");
            } catch (NumberFormatException e) {
                status.set("Stock must be an integer");
            }
        });

        providerIdText.addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                // Allow blank → providerId = null
                status.set(""); 
                return;
            }
            try {
                int pid = Integer.parseInt(n);
                if (pid < 0) status.set("Provider ID must be >= 0");
                else status.set("");
            } catch (NumberFormatException e) {
                status.set("Provider ID must be an integer");
            }
        });

    }

    // Async operations (use a background thread; minimal error handling)
    public void loadProductsAsync() {
        status.set("Loading products...");
        new Thread(() -> {
            try {
                List<Product> list = service.getAllProducts();
                Platform.runLater(() -> {
                    products.setAll(list);
                    status.set("Loaded " + list.size() + " products");
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Load failed: " + e.getMessage()));
            }
        }, "load-products").start();
    }

    public void createProductAsync() {
        String nm = name.get().trim();
        String desc = description.get().trim();
        double priceVal;
        try {
            priceVal = Double.parseDouble(priceText.get());
            if (priceVal < 0) throw new IllegalArgumentException("Price must be >= 0");
        } catch (Exception e) {
            status.set("Invalid price: " + e.getMessage());
            return;
        }
        int stockVal;
        try {
            stockVal = Integer.parseInt(stockText.get());
            if (stockVal < 0) throw new IllegalArgumentException("Stock must be >= 0");
        } catch (Exception e) {
            status.set("Invalid stock: " + e.getMessage());
            return;
        }
        if (nm.isBlank()) {
            status.set("Name is required");
            return;
        }
        final Integer providerIdVal;
        if (!providerIdText.get().isBlank()) {
            Integer tmp;
            try {
                tmp = Integer.parseInt(providerIdText.get());
                if (tmp < 0) throw new IllegalArgumentException("Provider ID must be >= 0");
            } catch (Exception e) {
                status.set("Invalid provider ID: " + e.getMessage());
                return;
            }
            providerIdVal = tmp;
        } else {
            providerIdVal = null;
        }

        status.set("Creating product...");
        new Thread(() -> {
            try {
                Product created = service.createProduct(nm, desc.isBlank() ? null : desc, priceVal, stockVal, providerIdVal);
                Platform.runLater(() -> {
                    products.add(0, created);
                    setSelectedProduct(created);
                    status.set("Created product ID " + created.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Create failed: " + e.getMessage()));
            }
        }, "create-product").start();
    }

    public void updateSelectedProductAsync() {
        Product sel = selectedProduct.get();
        if (sel == null) {
            status.set("No product selected");
            return;
        }
        String nm = name.get().trim();
        String desc = description.get().trim();
        double priceVal;
        try {
            priceVal = Double.parseDouble(priceText.get());
            if (priceVal < 0) throw new IllegalArgumentException("Price must be >= 0");
        } catch (Exception e) {
            status.set("Invalid price: " + e.getMessage());
            return;
        }
        int stockVal;
        try {
            stockVal = Integer.parseInt(stockText.get());
            if (stockVal < 0) throw new IllegalArgumentException("Stock must be >= 0");
        } catch (Exception e) {
            status.set("Invalid stock: " + e.getMessage());
            return;
        }
        if (nm.isBlank()) {
            status.set("Name is required");
            return;
        }
        final Integer providerIdVal;
        if (!providerIdText.get().isBlank()) {
            Integer tmp;
            try {
                tmp = Integer.parseInt(providerIdText.get());
                if (tmp < 0) throw new IllegalArgumentException("Provider ID must be >= 0");
            } catch (Exception e) {
                status.set("Invalid provider ID: " + e.getMessage());
                return;
            }
            providerIdVal = tmp;
        } else {
            providerIdVal = null;
        }

        final int id = sel.getId();
        status.set("Updating product...");
        new Thread(() -> {
            try {
                Product updated = service.updateProduct(
                    id, 
                    nm, 
                    desc.isBlank() ? null : desc, 
                    priceVal, 
                    stockVal, 
                    providerIdVal
                );
                Platform.runLater(() -> {
                    // Replace in list
                    int idx = products.indexOf(sel);
                    if (idx >= 0) products.set(idx, updated);
                    setSelectedProduct(updated);
                    status.set("Updated product ID " + updated.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Update failed: " + e.getMessage()));
            }
        }, "update-product").start();
    }

    public void deleteSelectedProductAsync() {
        Product sel = selectedProduct.get();
        if (sel == null) {
            status.set("No product selected");
            return;
        }
        final int id = sel.getId();
        status.set("Deleting product...");
        new Thread(() -> {
            try {
                service.deleteProduct(id);
                Platform.runLater(() -> {
                    products.remove(sel);
                    setSelectedProduct(null);
                    status.set("Deleted product ID " + id);
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Delete failed: " + e.getMessage()));
            }
        }, "delete-product").start();
    }

    private void clearForm() {
        name.set("");
        description.set("");
        priceText.set("");
        stockText.set("");//4
        providerIdText.set("");
        createdAt.set("");
        updatedAt.set("");
    }

    // Exposed properties for binding
    public ObservableList<Product> getProducts() { return products; }
    public ObjectProperty<Product> selectedProductProperty() { return selectedProduct; }
    public void setSelectedProduct(Product p) { selectedProduct.set(p); }
    public BooleanProperty hasSelectionProperty() { return hasSelection; }

    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty priceTextProperty() { return priceText; }
    public StringProperty stockTextProperty() { return stockText; }//2
    public StringProperty providerIdTextProperty() { return providerIdText; }
    public StringProperty createdAtProperty() { return createdAt; }
    public StringProperty updatedAtProperty() { return updatedAt; }

    public StringProperty statusProperty() { return status; }
}

package com.demo.ui.controllers;

import com.demo.ui.models.Product;
import com.demo.ui.viewmodels.ProductsViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductsController {

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> idCol;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, String> descriptionCol;
    @FXML private TableColumn<Product, Double> priceCol;
    @FXML private TableColumn<Product, Integer> stockCol;//
    @FXML private TableColumn<Product, Integer> providerIdCol;//
    @FXML private TableColumn<Product, String> createdAtCol;//
    @FXML private TableColumn<Product, String> updatedAtCol;//

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField stockField;//
    @FXML private TextField providerIdField;//
    @FXML private Label createdAtLabel;//
    @FXML private Label updatedAtLabel;//

    @FXML private Button loadBtn;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Label statusLabel;

    private final ProductsViewModel vm = new ProductsViewModel();

    @FXML
    public void initialize() {
        // Table columns -> model getters
        idCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        nameCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName())
        );
        descriptionCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getDescription())
        );
        priceCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getPrice()).asObject()
        );
        stockCol.setCellValueFactory(data ->//
            new SimpleIntegerProperty(data.getValue().getStock()).asObject()
        );
        providerIdCol.setCellValueFactory(data -> {
        Integer pid = data.getValue().getProviderId();
        return new SimpleIntegerProperty(pid != null ? pid : 0).asObject();
         });
        createdAtCol.setCellValueFactory(data -> {
            String ca = data.getValue().getCreatedAt();
            return new SimpleStringProperty(ca != null ? ca : "");
        });
        updatedAtCol.setCellValueFactory(data -> {
            String ua = data.getValue().getUpdatedAt();
            return new SimpleStringProperty(ua != null ? ua : "");
        });
        
        // Table items
        productsTable.setItems(vm.getProducts());

        // Selection -> ViewModel selectedProduct
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            vm.setSelectedProduct(sel);
        });

        // Form bindings (bidirectional)
        nameField.textProperty().bindBidirectional(vm.nameProperty());
        descriptionArea.textProperty().bindBidirectional(vm.descriptionProperty());
        priceField.textProperty().bindBidirectional(vm.priceTextProperty());
        stockField.textProperty().bindBidirectional(vm.stockTextProperty());//
        providerIdField.textProperty().bindBidirectional(vm.providerIdTextProperty());//
        createdAtLabel.textProperty().bind(vm.createdAtProperty());//
        updatedAtLabel.textProperty().bind(vm.updatedAtProperty());//

        // Status
        statusLabel.textProperty().bind(vm.statusProperty());

        // Button states
        updateBtn.disableProperty().bind(vm.hasSelectionProperty().not());
        deleteBtn.disableProperty().bind(vm.hasSelectionProperty().not());

        // Initial load
        vm.loadProductsAsync();
    }

    @FXML
    private void onLoad() {
        vm.loadProductsAsync();
    }

    @FXML
    private void onAdd() {
        vm.createProductAsync();
    }

    @FXML
    private void onUpdate() {
        vm.updateSelectedProductAsync();
    }

    @FXML
    private void onDelete() {
        // Optional confirm dialog
        Product sel = productsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete product ID " + sel.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirm deletion");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) vm.deleteSelectedProductAsync();
        });
    }
}

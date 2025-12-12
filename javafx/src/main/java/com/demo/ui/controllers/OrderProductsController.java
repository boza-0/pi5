package com.demo.ui.controllers;

import com.demo.ui.models.OrderProduct;
import com.demo.ui.viewmodels.OrderProductsViewModel;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class OrderProductsController {

    @FXML private TableView<OrderProduct> orderProductsTable;
    @FXML private TableColumn<OrderProduct, Integer> idCol;
    @FXML private TableColumn<OrderProduct, Integer> orderIdCol;
    @FXML private TableColumn<OrderProduct, Integer> productIdCol;
    @FXML private TableColumn<OrderProduct, Integer> quantityCol;
    @FXML private TableColumn<OrderProduct, Double> unitPriceCol;
    @FXML private TableColumn<OrderProduct, Double> lineTotalCol;

    @FXML private TextField orderIdField;
    @FXML private TextField productIdField;
    @FXML private TextField quantityField;
    @FXML private TextField unitPriceField;
    @FXML private Label lineTotalLabel;

    @FXML private Button loadBtn;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button removeBtn;
    @FXML private Label statusLabel;

    private final OrderProductsViewModel vm = new OrderProductsViewModel();

    @FXML
    public void initialize() {
        // Table columns -> model getters
        idCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        orderIdCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getOrderId()).asObject()
        );
        productIdCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getProductId()).asObject()
        );
        quantityCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject()
        );
        unitPriceCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject()
        );
        lineTotalCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getLineTotal()).asObject()
        );

        // Table items
        orderProductsTable.setItems(vm.getOrderProducts());

        // Selection -> ViewModel selectedItem
        orderProductsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            vm.setSelectedItem(sel);
        });

        // Form bindings (bidirectional)
        orderIdField.textProperty().bindBidirectional(vm.orderIdProperty(), new NumberStringConverter());
        productIdField.textProperty().bindBidirectional(vm.productIdProperty(), new NumberStringConverter());
        quantityField.textProperty().bindBidirectional(vm.quantityProperty(), new NumberStringConverter());
        unitPriceField.textProperty().bindBidirectional(vm.unitPriceProperty(), new NumberStringConverter());
        lineTotalLabel.textProperty().bind(vm.lineTotalProperty().asString());

        // Status
        statusLabel.textProperty().bind(vm.statusProperty());

        // Button states
        updateBtn.disableProperty().bind(vm.hasSelectionProperty().not());
        removeBtn.disableProperty().bind(vm.hasSelectionProperty().not());
    }

    @FXML
    private void onLoad() {
        int oid = vm.orderIdProperty().get();
        if (oid > 0) {
            vm.loadProductsForOrderAsync(oid);
        } else {
            vm.statusProperty().set("Enter a valid Order ID to load products");
        }
    }

    @FXML
    private void onAdd() {
        int oid = vm.orderIdProperty().get();
        if (oid > 0) {
            vm.addProductAsync(oid);
        } else {
            vm.statusProperty().set("Enter a valid Order ID before adding");
        }
    }

    @FXML
    private void onUpdate() {
        int oid = vm.orderIdProperty().get();
        if (oid > 0) {
            vm.updateSelectedItemAsync(oid);
        } else {
            vm.statusProperty().set("Enter a valid Order ID before updating");
        }
    }

    @FXML
    private void onRemove() {
        OrderProduct sel = orderProductsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        int oid = vm.orderIdProperty().get();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove product ID " + sel.getProductId() + " from order " + oid + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirm removal");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) vm.removeSelectedItemAsync(oid);
        });
    }
}

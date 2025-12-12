package com.demo.ui.controllers;

import com.demo.ui.models.Order;
import com.demo.ui.viewmodels.OrdersViewModel;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OrdersController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> idCol;
    @FXML private TableColumn<Order, String> orderNumberCol;
    @FXML private TableColumn<Order, Integer> clientIdCol;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private TableColumn<Order, String> statusCol;
    @FXML private TableColumn<Order, String> paymentCol;
    @FXML private TableColumn<Order, String> currencyCol;
    @FXML private TableColumn<Order, Double> subtotalCol;
    @FXML private TableColumn<Order, Double> discountCol;
    @FXML private TableColumn<Order, Double> taxCol;
    @FXML private TableColumn<Order, Double> totalCol;
    @FXML private TableColumn<Order, String> createdAtCol;
    @FXML private TableColumn<Order, String> updatedAtCol;

    @FXML private TextField orderNumberField;
    @FXML private TextField clientIdField;
    @FXML private TextField statusField;
    @FXML private TextField paymentField;
    @FXML private TextField currencyField;
    @FXML private TextField shippingField;
    @FXML private TextField billingField;
    @FXML private TextArea notesArea;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;

    @FXML private Button loadBtn;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Label statusLabel;

    private final OrdersViewModel vm = new OrdersViewModel();

    @FXML
    public void initialize() {
        // Table columns -> model getters
        idCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        orderNumberCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getOrderNumber())
        );
        clientIdCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getClientId()).asObject()
        );
        orderDateCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getOrderDate() != null ? data.getValue().getOrderDate() : "")
        );
        statusCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getOrderStatus())
        );
        paymentCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPaymentMethod())
        );
        currencyCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCurrencyCode())
        );
        subtotalCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getSubtotalAmount()).asObject()
        );
        discountCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getDiscountAmount()).asObject()
        );
        taxCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getTaxAmount()).asObject()
        );
        totalCol.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject()
        );
        createdAtCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt() : "")
        );
        updatedAtCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUpdatedAt() != null ? data.getValue().getUpdatedAt() : "")
        );

        // Table items
        ordersTable.setItems(vm.getOrders());

        // Selection -> ViewModel selectedOrder
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            vm.setSelectedOrder(sel);
        });

        // Form bindings (bidirectional)
        orderNumberField.textProperty().bindBidirectional(vm.orderNumberProperty());
        clientIdField.textProperty().bindBidirectional(vm.clientIdProperty(), new javafx.util.converter.NumberStringConverter());
        statusField.textProperty().bindBidirectional(vm.orderStatusProperty());
        paymentField.textProperty().bindBidirectional(vm.paymentMethodProperty());
        currencyField.textProperty().bindBidirectional(vm.currencyCodeProperty());
        shippingField.textProperty().bindBidirectional(vm.shippingAddressProperty());
        billingField.textProperty().bindBidirectional(vm.billingAddressProperty());
        notesArea.textProperty().bindBidirectional(vm.notesProperty());
        createdAtLabel.textProperty().bind(vm.createdAtProperty());
        updatedAtLabel.textProperty().bind(vm.updatedAtProperty());

        // Status
        statusLabel.textProperty().bind(vm.statusProperty());

        // Button states
        updateBtn.disableProperty().bind(vm.hasSelectionProperty().not());
        deleteBtn.disableProperty().bind(vm.hasSelectionProperty().not());

        // Initial load
        vm.loadOrdersAsync();
    }

    @FXML
    private void onLoad() {
        vm.loadOrdersAsync();
    }

    @FXML
    private void onAdd() {
        vm.createOrderAsync();
    }

    @FXML
    private void onUpdate() {
        vm.updateSelectedOrderAsync();
    }

    @FXML
    private void onDelete() {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete order ID " + sel.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirm deletion");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) vm.deleteSelectedOrderAsync();
        });
    }
}

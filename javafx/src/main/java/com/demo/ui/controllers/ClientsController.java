package com.demo.ui.controllers;

import com.demo.ui.models.Client;
import com.demo.ui.viewmodels.ClientsViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientsController {

    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Integer> idCol;
    @FXML private TableColumn<Client, String> nameCol;
    @FXML private TableColumn<Client, String> emailCol;
    @FXML private TableColumn<Client, String> phoneCol;
    @FXML private TableColumn<Client, String> addressCol;
    @FXML private TableColumn<Client, String> createdAtCol;
    @FXML private TableColumn<Client, String> updatedAtCol;

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private Label createdAtLabel;
    @FXML private Label updatedAtLabel;

    @FXML private Button loadBtn;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;
    @FXML private Label statusLabel;

    private final ClientsViewModel vm = new ClientsViewModel();

    @FXML
    public void initialize() {
        // Table columns -> model getters
        idCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getId()).asObject()
        );
        nameCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName())
        );
        emailCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEmail())
        );
        phoneCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPhone())
        );
        addressCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAddress())
        );
        createdAtCol.setCellValueFactory(data -> {
            String ca = data.getValue().getCreatedAt();
            return new SimpleStringProperty(ca != null ? ca : "");
        });
        updatedAtCol.setCellValueFactory(data -> {
            String ua = data.getValue().getUpdatedAt();
            return new SimpleStringProperty(ua != null ? ua : "");
        });

        // Table items
        clientsTable.setItems(vm.getClients());

        // Selection -> ViewModel selectedClient
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            vm.setSelectedClient(sel);
        });

        // Form bindings (bidirectional)
        nameField.textProperty().bindBidirectional(vm.nameProperty());
        emailField.textProperty().bindBidirectional(vm.emailProperty());
        phoneField.textProperty().bindBidirectional(vm.phoneProperty());
        addressField.textProperty().bindBidirectional(vm.addressProperty());
        createdAtLabel.textProperty().bind(vm.createdAtProperty());
        updatedAtLabel.textProperty().bind(vm.updatedAtProperty());

        // Status
        statusLabel.textProperty().bind(vm.statusProperty());

        // Button states
        updateBtn.disableProperty().bind(vm.hasSelectionProperty().not());
        deleteBtn.disableProperty().bind(vm.hasSelectionProperty().not());

        // Initial load
        vm.loadClientsAsync();
    }

    @FXML
    private void onLoad() {
        vm.loadClientsAsync();
    }

    @FXML
    private void onAdd() {
        vm.createClientAsync();
    }

    @FXML
    private void onUpdate() {
        vm.updateSelectedClientAsync();
    }

    @FXML
    private void onDelete() {
        Client sel = clientsTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete client ID " + sel.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Confirm deletion");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) vm.deleteSelectedClientAsync();
        });
    }
}

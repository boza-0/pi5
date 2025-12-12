package com.demo.ui.viewmodels;

import com.demo.ui.models.Client;
import com.demo.ui.services.ClientService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ClientsViewModel {

    private final ClientService service = new ClientService();

    // Table data
    private final ObservableList<Client> clients = FXCollections.observableArrayList();

    // Selection
    private final ObjectProperty<Client> selectedClient = new SimpleObjectProperty<>(null);
    private final BooleanProperty hasSelection = new SimpleBooleanProperty(false);

    // Form fields
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");
    private final StringProperty address = new SimpleStringProperty("");
    private final StringProperty createdAt = new SimpleStringProperty("");
    private final StringProperty updatedAt = new SimpleStringProperty("");

    // Status message
    private final StringProperty status = new SimpleStringProperty("");

    public ClientsViewModel() {
        // When selection changes, populate form fields
        selectedClient.addListener((obs, old, sel) -> {
            hasSelection.set(sel != null);
            if (sel != null) {
                name.set(sel.getName() != null ? sel.getName() : "");
                email.set(sel.getEmail() != null ? sel.getEmail() : "");
                phone.set(sel.getPhone() != null ? sel.getPhone() : "");
                address.set(sel.getAddress() != null ? sel.getAddress() : "");
                createdAt.set(sel.getCreatedAt() != null ? sel.getCreatedAt() : "");
                updatedAt.set(sel.getUpdatedAt() != null ? sel.getUpdatedAt() : "");
            } else {
                clearForm();
            }
        });

        // Simple validation feedback
        email.addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                status.set("Email is required");
            } else if (!n.contains("@")) {
                status.set("Invalid email format");
            } else {
                status.set("");
            }
        });
    }

    // Async operations
    public void loadClientsAsync() {
        status.set("Loading clients...");
        new Thread(() -> {
            try {
                List<Client> list = service.getAllClients();
                Platform.runLater(() -> {
                    clients.setAll(list);
                    status.set("Loaded " + list.size() + " clients");
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Load failed: " + e.getMessage()));
            }
        }, "load-clients").start();
    }

    public void createClientAsync() {
        String nm = name.get().trim();
        String em = email.get().trim();
        String ph = phone.get().trim();
        String addr = address.get().trim();

        if (nm.isBlank()) {
            status.set("Name is required");
            return;
        }
        if (em.isBlank() || !em.contains("@")) {
            status.set("Valid email is required");
            return;
        }

        status.set("Creating client...");
        new Thread(() -> {
            try {
                Client created = service.createClient(nm, em, ph.isBlank() ? null : ph, addr.isBlank() ? null : addr);
                Platform.runLater(() -> {
                    clients.add(0, created);
                    setSelectedClient(created);
                    status.set("Created client ID " + created.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Create failed: " + e.getMessage()));
            }
        }, "create-client").start();
    }

    public void updateSelectedClientAsync() {
        Client sel = selectedClient.get();
        if (sel == null) {
            status.set("No client selected");
            return;
        }

        String nm = name.get().trim();
        String em = email.get().trim();
        String ph = phone.get().trim();
        String addr = address.get().trim();

        if (nm.isBlank()) {
            status.set("Name is required");
            return;
        }
        if (em.isBlank() || !em.contains("@")) {
            status.set("Valid email is required");
            return;
        }

        final int id = sel.getId();
        status.set("Updating client...");
        new Thread(() -> {
            try {
                Client updated = service.updateClient(id, nm, em, ph.isBlank() ? null : ph, addr.isBlank() ? null : addr);
                Platform.runLater(() -> {
                    int idx = clients.indexOf(sel);
                    if (idx >= 0) clients.set(idx, updated);
                    setSelectedClient(updated);
                    status.set("Updated client ID " + updated.getId());
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Update failed: " + e.getMessage()));
            }
        }, "update-client").start();
    }

    public void deleteSelectedClientAsync() {
        Client sel = selectedClient.get();
        if (sel == null) {
            status.set("No client selected");
            return;
        }
        final int id = sel.getId();
        status.set("Deleting client...");
        new Thread(() -> {
            try {
                service.deleteClient(id);
                Platform.runLater(() -> {
                    clients.remove(sel);
                    setSelectedClient(null);
                    status.set("Deleted client ID " + id);
                });
            } catch (Exception e) {
                Platform.runLater(() -> status.set("Delete failed: " + e.getMessage()));
            }
        }, "delete-client").start();
    }

    private void clearForm() {
        name.set("");
        email.set("");
        phone.set("");
        address.set("");
        createdAt.set("");
        updatedAt.set("");
    }

    // Exposed properties for binding
    public ObservableList<Client> getClients() { return clients; }
    public ObjectProperty<Client> selectedClientProperty() { return selectedClient; }
    public void setSelectedClient(Client c) { selectedClient.set(c); }
    public BooleanProperty hasSelectionProperty() { return hasSelection; }

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty addressProperty() { return address; }
    public StringProperty createdAtProperty() { return createdAt; }
    public StringProperty updatedAtProperty() { return updatedAt; }

    public StringProperty statusProperty() { return status; }
}

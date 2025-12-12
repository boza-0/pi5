package com.demo.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    private BorderPane root;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        root = new BorderPane();

        // Create menu bar
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().add(exitItem);

        // View menu
        Menu viewMenu = new Menu("View");

        MenuItem productsItem = new MenuItem("Products");
        productsItem.setOnAction(e -> loadView("/com/demo/ui/ProductsView.fxml", "Products"));

        MenuItem clientsItem = new MenuItem("Clients");
        clientsItem.setOnAction(e -> loadView("/com/demo/ui/ClientsView.fxml", "Clients"));

        MenuItem ordersItem = new MenuItem("Orders");
        ordersItem.setOnAction(e -> loadView("/com/demo/ui/OrdersView.fxml", "Orders"));

        MenuItem orderProductsItem = new MenuItem("Order Products");
        orderProductsItem.setOnAction(e -> loadView("/com/demo/ui/OrderProductsView.fxml", "Order Products"));

        viewMenu.getItems().addAll(productsItem, clientsItem, ordersItem, orderProductsItem);

        menuBar.getMenus().addAll(fileMenu, viewMenu);

        root.setTop(menuBar);

        // Initial view
        loadView("/com/demo/ui/ProductsView.fxml", "Products");

        Scene scene = new Scene(root, 1000, 700); // slightly larger for orders UI
        stage.setScene(scene);
        stage.setTitle("Demo UI");
        stage.show();
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root.setCenter(loader.load());
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

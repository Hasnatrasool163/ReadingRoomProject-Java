package controller;

import dao.OrderDaoImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.Order;
import model.OrderDetails;
import model.OrderDetailsView;
import model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersController {

    @FXML
    private TableView<OrderDetailsView> ordersTable;
    @FXML
    private TableColumn<OrderDetailsView, String> orderNumberColumn;
    @FXML
    private TableColumn<OrderDetailsView, String> dateTimeColumn;
    @FXML
    private TableColumn<OrderDetailsView, Double> totalPriceColumn;
    @FXML
    private TableColumn<OrderDetailsView, String> booksPurchasedColumn;
    @FXML
    private TableColumn<OrderDetailsView, Integer> quantityColumn;

    private ObservableList<OrderDetailsView> ordersList = FXCollections.observableArrayList();

    private OrderDaoImpl orderDao;

    User currentUser;
    @FXML
    public void initialize() {
        // Initialize table columns
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        booksPurchasedColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Load the user's orders
        orderDao = new OrderDaoImpl();
        try{
            currentUser= HomeController.currentUser;
            orderDao.setup();
            loadOrder();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadOrder() throws SQLException {
        List<Order> orders = orderDao.getOrdersWithDetailsByUserName(currentUser.getUsername());
        List<OrderDetailsView> orderDetailsViewList = new ArrayList<>();

        // Iterate through orders
        for (Order order : orders) {
            for (OrderDetails details : order.getOrderDetailsList()) {
                OrderDetailsView detailsView = new OrderDetailsView(
                        order.getOrderId(),
                        order.getFormattedDate(),
                        details.getTitle(),
                        details.getQuantity(),
                        order.getTotalPrice()
                );
                orderDetailsViewList.add(detailsView);
            }
        }
        ObservableList<OrderDetailsView> observableOrderDetails = FXCollections.observableArrayList(orderDetailsViewList);
        ordersTable.setItems(observableOrderDetails);
        ordersList.addAll(observableOrderDetails);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // export method
    @FXML
    public void handleExportOrders(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Orders as CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // save dialog
        File file = fileChooser.showSaveDialog(ordersTable.getScene().getWindow());

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(file)) {
                // Write header row
                writer.append("Order ID, Order Date, Book Title, Quantity, Total Price\n");

                // Write order data
                for (OrderDetailsView order : ordersList) {
                    writer.append(String.valueOf(order.getOrderId())).append(",")
                            .append(order.getOrderDate()).append(",")
                            .append(order.getBookTitle()).append(",")
                            .append(String.valueOf(order.getQuantity())).append(",")
                            .append(String.valueOf(order.getTotalPrice())).append("\n");
                }

                showAlert("Export Successful", "Orders were exported successfully to " + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Export Failed", "An error occurred while exporting the orders.");
            }
        }
    }


    @FXML
    private void handleLogout() {
        Stage stage = (Stage) ordersTable.getScene().getWindow();
        stage.close();
    }
}

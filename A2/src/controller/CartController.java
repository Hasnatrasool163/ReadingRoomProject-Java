package controller;


import dao.BookDaoImpl;
import dao.OrderDetailsDaoImpl;
import dao.ShoppingCartDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CartItem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static controller.HomeController.currentUser;

public class CartController {

    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> titleColumn;
    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<CartItem, Double> priceColumn;
    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private Button checkoutButton;
    @FXML Button deleteButton;

    private ObservableList<CartItem> cart;

    ShoppingCartDaoImpl shoppingCartDao;
    OrderDetailsDaoImpl orderDetailsDao;

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));


        checkoutButton.setOnAction(event -> handleCheckout());
        deleteButton.setOnAction(event -> handleDeleteItem());
        shoppingCartDao = new ShoppingCartDaoImpl();
        orderDetailsDao = new OrderDetailsDaoImpl();
        cart = FXCollections.observableArrayList();
//        cartTable.setItems(cart);
        try {
            shoppingCartDao.setup();
            orderDetailsDao.setup();
            loadCartItems();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }


    private void loadCartItems() throws SQLException {
        List<CartItem> cartItems = shoppingCartDao.getCartItemsByUserName(currentUser.getUsername());
        cart.clear();
        cart.addAll(cartItems);
        cartTable.getItems().clear();
        cartTable.getItems().addAll(cartItems);
    }

    @FXML
    private void handleCheckout() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Book Selected", "Please select a book to proceed with checkout.");
            return;
        }

        ObservableList<CartItem> allCartItems = cartTable.getItems();
        double totalCartPrice = allCartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Order");
        confirmationAlert.setHeaderText("Are you sure you want to proceed with a total price of: " + totalCartPrice + "?");
        confirmationAlert.setContentText("This will place an order for all items in the cart. This action cannot be undone.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showCheckoutModal(allCartItems, totalCartPrice);
            }
            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.close();  // Close the current window
        });
    }


    private void showCheckoutModal(ObservableList<CartItem> allCartItems, double totalCartPrice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Checkout.fxml"));
            CheckoutController checkoutController = new CheckoutController(allCartItems, totalCartPrice);  // Send all items and total price
            loader.setController(checkoutController);

            Pane root = loader.load();

            // Create a modal stage
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(cartTable.getScene().getWindow());
            modalStage.setTitle("Checkout");

            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private void showCheckoutModal(CartItem selectedItem) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Checkout.fxml"));
//            CheckoutController checkoutController = new CheckoutController(selectedItem);
//            loader.setController(checkoutController);
//
//            Pane root = loader.load();
//
//            // Create a modal stage
//            Stage modalStage = new Stage();
//            modalStage.initModality(Modality.APPLICATION_MODAL);
//            modalStage.initOwner(cartTable.getScene().getWindow());
//            modalStage.setTitle("Checkout");
//
//            checkoutController.setCartItem(selectedItem);
//
//            Scene scene = new Scene(root);
//            modalStage.setScene(scene);
//            modalStage.setResizable(false);
//            modalStage.centerOnScreen();
//            modalStage.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void handleDeleteItem() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Book Selected", "Please select a book to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Item");
        confirmationAlert.setHeaderText("Are you sure you want to remove this item?");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    BookDaoImpl dao = new BookDaoImpl();
                    int bookId = dao.getBookIdByTitle(selectedItem.getTitle());
                    shoppingCartDao.removeCartItem(shoppingCartDao.getUserId(currentUser.getUsername()), bookId);
                    cart.remove(selectedItem);
                    cartTable.refresh();
                    showAlert("Item Removed", "The item has been removed from your cart.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

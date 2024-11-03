package controller;


import dao.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.CartItem;
import model.Order;
import model.OrderDetails;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static controller.HomeController.currentUser;


public class CheckoutController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private DatePicker expiryDateField;

    @FXML
    private TextField cvvField;

    @FXML
    private CartItem cartItem;

    @FXML
    private Button checkoutButton;

    UserDaoImpl userDao = new UserDaoImpl();
    OrderDaoImpl orderDao = new OrderDaoImpl();


    private ObservableList<CartItem> cartItems;
    private double totalCartPrice;

    public CheckoutController(ObservableList<CartItem> cartItems, double totalCartPrice) {
        this.cartItems = cartItems;
        this.totalCartPrice = totalCartPrice;
    }


    @FXML
    public void initialize(){
        checkoutButton.setOnAction(event -> handleCheckout());
    }

    @FXML
    private void handleCheckout() {
        String cardNumber = cardNumberField.getText().trim();
        String expiryDate = String.valueOf(expiryDateField.getValue());
        String cvv = cvvField.getText().trim();

        if (!validateCardNumber(cardNumber) || !validateExpiryDate(expiryDate) || !validateCVV(cvv)) {
            showAlert("Invalid Input", "Please check your payment details.");
            return;
        }

        try {
            double totalPrice = totalCartPrice;

            int userId = userDao.getUserIdByUsername(currentUser.getUsername());

            Order order = new Order(userId, LocalDateTime.now(), totalPrice);
            orderDao.addOrder(order);

            int orderId = orderDao.getLatestOrderId(userId);

            OrderDetailsDaoImpl orderDetailsDao = new OrderDetailsDaoImpl();
            BookDaoImpl bookDao = new BookDaoImpl();

            for (CartItem cartItem : cartItems) {
                int bookId = bookDao.getBookIdByTitle(cartItem.getTitle());
                OrderDetails orderDetails = new OrderDetails(orderId, bookId, cartItem.getQuantity(), cartItem.getPrice(), cartItem.getTitle());
                orderDetailsDao.addOrderDetail(orderDetails);

                ShoppingCartDaoImpl shoppingCartDao = new ShoppingCartDaoImpl();
                shoppingCartDao.removeCartItem(userId, bookId);
            }

            showAlert("Checkout Successful", "Thank you for your purchase!");

            Stage stage = (Stage) cardNumberField.getScene().getWindow();
            stage.close(); // Close the modal

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Checkout Failed", "An error occurred while processing your order.");
        }
    }

    private boolean validateCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}");
    }

    private boolean validateExpiryDate(String expiryDate) {
        try {
            // Date from DatePicker
            LocalDate date = LocalDate.parse(expiryDate);
            return date.isAfter(LocalDate.now());  // Check if expiry date is in the future
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateCVV(String cvv) {
        return cvv.matches("\\d{3}");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}

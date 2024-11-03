package controller;


import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import model.Book;
import model.CartItem;

import model.ShoppingCart;

import java.sql.SQLException;
import java.util.List;


public class BookstoreController {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Double> priceColumn;
    @FXML
    private TableColumn<Book, Integer> stockColumn;
    @FXML
    private Spinner<Integer> quantitySpinner;

    private static ObservableList<CartItem> cart = FXCollections.observableArrayList();

    BookDaoImpl bookDao;
    ShoppingCartDaoImpl shoppingCartDao;
    UserDaoImpl userDao;
    @FXML
    public void initialize() {
        // Initialize table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("physicalCopies"));
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        userDao = new UserDaoImpl();
        bookDao = new BookDaoImpl();
        shoppingCartDao = new ShoppingCartDaoImpl();
        try {
            userDao.setup();
            bookDao.setup();
            shoppingCartDao.setup();
            loadBooks();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadBooks() throws SQLException {
            List<Book> books = bookDao.getAllBooks();
            bookTable.getItems().clear();
            bookTable.getItems().addAll(books);
    }

    @FXML
    private void handleAddToCart() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            int quantity = quantitySpinner.getValue();

            if (quantity > selectedBook.getPhysicalCopies()) {
                showAlert("Stock Unavailable", "Not enough stock for the selected book.");
            } else {
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirmation");
                    confirmationAlert.setHeaderText("Are you sure you want to add this book: " + selectedBook.getTitle() + "?");
                    confirmationAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                int bookId = bookDao.getBookIdByTitle(selectedBook.getTitle()); // Get book ID
                                int userId = userDao.getUserIdByUsername(HomeController.currentUser.getUsername()); // Get user ID

                                // Create a new cart item and save it in the database
                                ShoppingCart cartItem = new ShoppingCart(userId, bookId, quantity);
                                shoppingCartDao.addOrUpdateCartItem(cartItem);

                                // Update stock in books table
                                bookDao.updateStock(selectedBook.getTitle(), selectedBook.getPhysicalCopies() - quantity);
                                selectedBook.setSoldCopies(selectedBook.getSoldCopies() + quantity);
                                selectedBook.setPhysicalCopies(selectedBook.getPhysicalCopies() - quantity);  // Update
                                bookTable.refresh(); // Refresh updated stock
                                showAlert("Added to Cart", quantity + " copies of " + selectedBook.getTitle() + " added to cart.");
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
        }
        else{
            showAlert("No Selection","Select Any Book First!");
        }
        }


    @FXML
    private void handleViewCart() {
        changeScene("/view/CartView.fxml", "Your Shopping Cart");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static void changeScene(String fxmlFile, String title) {
        Main.switchScene(fxmlFile, title);
    }

    public static ObservableList<CartItem> getCart() {
        return cart;
    }
}

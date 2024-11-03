package controller;

import dao.BookDaoImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Book;
import model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, Double> priceColumn;
    @FXML
    private TableColumn<Book, Integer> soldCopiesColumn;
    @FXML
    private TableColumn<Book, Integer> availableStockColumn;
    @FXML
    private TextField stockTextField;
    @FXML
    private Button add_book;
    @FXML
    private Button delete_book;
    @FXML
    private Button logout;
    @FXML
    private Button update_stock;

    private Stage stage;
    private Model model;
    private Stage parentStage;


    private BookDaoImpl bookDao;

    /**
     * Key Points of the Singleton Implementation:
     *     Private Constructor: The constructor is made private to ensure no other class can instantiate
     *     this class directly.
     *     Static Instance: A static field instance is used to store the single instance of the class.
     *     getInstance Method: This method checks if the instance is null. If so,
     *     it creates a new AdminDashboardController. Otherwise, it returns the already existing instance.
     */
    private static AdminDashboardController instance;

    public static AdminDashboardController getInstance(Stage parentStage, Model model) {
        if (instance == null) {
            instance = new AdminDashboardController(parentStage, model);
        }
        return instance;
    }

    private AdminDashboardController(Stage parentStage , Model model) {
        this.stage = new Stage();
        this.parentStage = parentStage;
        this.model = model;
    }

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        soldCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("soldCopies"));
        availableStockColumn.setCellValueFactory(new PropertyValueFactory<>("physicalCopies"));

        bookDao = new BookDaoImpl();
        try {
            bookDao.setup();
            loadBooks();

            // Add listener to update the stockTextField when a book is selected
            bookTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Book>() {
                @Override
                public void changed(ObservableValue<? extends Book> observable, Book oldValue, Book newValue) {
                    if (newValue != null) {
                        stockTextField.setText(String.valueOf(newValue.getPhysicalCopies()));
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        add_book.setOnAction(event -> addBook());
        update_stock.setOnAction(event -> handleUpdateStock());
        logout.setOnAction(event -> logout());
        delete_book.setOnAction(event -> handleDeleteBook());
    }

    private void loadBooks() throws SQLException {
        List<Book> books = bookDao.getAllBooks();
        bookTable.getItems().clear();
        bookTable.getItems().addAll(books);
    }

    private void handleDeleteBook(){
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Book Selected", "Please select a book to delete.");
            return;
        }

        // Confirmation Alert
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Are you sure you want to delete the book: " + selectedBook.getTitle() + "?");
        confirmationAlert.setContentText("This action cannot be undone.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from database
                    bookDao.deleteBook(selectedBook.getTitle());
                    // Remove from  TableView
                    bookTable.getItems().remove(selectedBook);
                    stockTextField.setText("");

                    showAlert("Success", "Book deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to delete book.");
                }
            }
        });
    }

    @FXML
    private void handleUpdateStock() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Book Selected", "Please select a book to update the stock.");
            return;
        }

        String newStockStr = stockTextField.getText();
        if (newStockStr.isEmpty() || !newStockStr.matches("\\d+")) {
            showAlert("Invalid Stock", "Please enter a valid stock quantity.");
            return;
        }

        int newStock = Integer.parseInt(newStockStr);
        try {
            bookDao.updateStock(selectedBook.getTitle(), newStock);
            selectedBook.setPhysicalCopies(newStock);  // Update
            bookTable.refresh(); // Refresh updated stock
            showAlert("Success", "Stock updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update stock.");
        }
    }

    @FXML
    private void logout() {
        stage.close();
        try {
            model.setup();
        model.setup();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

        // Customize controller instance
        Stage stage = new Stage();
            setupStage(loader, stage, model);

        } catch (IOException | SQLException | RuntimeException e) {
        Scene scene = new Scene(new Label(e.getMessage()), 200, 100);
        stage.setTitle("Error");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    }

    public static void setupStage(FXMLLoader loader, Stage stage, Model model) throws IOException {
        LoginController loginController = new LoginController(stage, model);

        loader.setController(loginController);
        Pane root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void changeScene(String fxmlFile, String title) {
        BookstoreController.changeScene(fxmlFile, title);
    }

    @FXML
    private void addBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddBook.fxml"));


            Stage addBookStage = new Stage();
            addBookStage.initModality(Modality.APPLICATION_MODAL);
            addBookStage.initOwner(stage);
            addBookStage.setTitle("Add Book");

            AddBookController controller = new AddBookController(addBookStage,bookTable.getItems());
            loader.setController(controller);
            Pane root = loader.load();

            Scene scene = new Scene(root);
            addBookStage.setScene(scene);
            addBookStage.setResizable(false);
            addBookStage.centerOnScreen();
            addBookStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showStage(Pane root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Home");
        stage.centerOnScreen();
        stage.show();
    }
}

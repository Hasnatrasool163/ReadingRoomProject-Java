package controller;

import dao.BookDaoImpl;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Book;

import java.sql.SQLException;

public class AddBookController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button closeButton;

    private Stage stage;
    private BookDaoImpl bookDao;
    private ObservableList<Book> bookList;

    public AddBookController(Stage stage,ObservableList<Book> bookList) {
        bookDao = new BookDaoImpl(); // Initialize
        this.bookList =bookList;
        this.stage=stage;
    }


    @FXML
    private void initialize() {
        addButton.setOnAction(event -> handleAddBook());
        closeButton.setOnAction(event -> stage.close());
    }

    @FXML
    private void handleAddBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();

        // validation
        if (title.isEmpty() || author.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            messageLabel.setText("All fields must be filled");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            // Create a new book object add it to the database
            Book book = new Book(title, author, quantity, price, 0, false);
            bookDao.addBook(book);
            bookList.add(book);

            messageLabel.setTextFill(javafx.scene.paint.Paint.valueOf("green"));
            messageLabel.setText("Book added successfully!");
            clearFields();
//            stage.close();

        } catch (NumberFormatException e) {
            messageLabel.setText("Quantity and Price must be valid numbers");
        } catch (SQLException e) {
            messageLabel.setText("Failed to add book");
            e.printStackTrace();
        }
    }

    // Method to clear the input fields
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        quantityField.clear();
        priceField.clear();
    }
}

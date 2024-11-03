package controller;

import dao.UserDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

import java.sql.SQLException;


public class EditProfileController {

    @FXML
    private Label message;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;

    private UserDaoImpl userDao;
    private User currentUser;

    @FXML
    public void initialize() {
        userDao = new UserDaoImpl();
        currentUser = HomeController.currentUser;

        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            passwordField.setText(currentUser.getPassword());
        }
        else{
            System.out.println("Not logged in");
        }
    }

    @FXML
    private void handleSaveChanges() {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = passwordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            message.setText("All fields must be filled");
            return;
        }

        // Update the current user's information
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setPassword(password);

        try {
            // Update the user in the database
            userDao.updateUser(currentUser);
            showAlert("Success", "Profile updated successfully!");
            message.setText("Profile updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update profile.");
            message.setText("Failed to update profile.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

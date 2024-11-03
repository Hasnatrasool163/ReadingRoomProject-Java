package controller;

import dao.BookDaoImpl;
import dao.OrderDaoImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import model.Book;
import model.Model;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	private Model model;
	private Stage stage;
	private Stage parentStage;
	@FXML
	private MenuItem viewProfile; // Corresponds to the Menu item "viewProfile" in Dashboard.fxml
	@FXML
	private MenuItem logout;
	@FXML
	private Label welcomeLabel1;
	@FXML
	private VBox dashboardContent;
	@FXML
	private Button editProfileButton;
	@FXML
	private Button viewOrdersButton;
	@FXML
	private Button logoutButton;

	@FXML
	private Button viewBookStore;

	@FXML
	private Button viewCart;
	@FXML
	private ListView<Book> popularBooksList;

	static User currentUser;

	private BookDaoImpl bookDao;
	private OrderDaoImpl orderDao;

	public HomeController(Stage parentStage, Model model) {
		this.stage = new Stage();
		this.parentStage = parentStage;
		this.model = model;
	}

	@FXML
	private void initialize() throws SQLException {
		if (model.getCurrentUser() != null) {
			currentUser= model.getCurrentUser();
			welcomeLabel1.setText(model.getCurrentUser().getFirstName());
		}

		bookDao = new BookDaoImpl();
		orderDao = new OrderDaoImpl();

		try {
			bookDao.setup();
			orderDao.setup();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		// Fetch top 5 books
		List<Book> popularBooks = bookDao.getTop5Books();
		updatePopularBooks(popularBooks);

//		 button actions
		editProfileButton.setOnAction(event -> handleEditProfile());
		viewCart.setOnAction(event -> handleViewCart());
		viewBookStore.setOnAction(event -> handleViewBookStore());
		viewOrdersButton.setOnAction(event -> handleViewOrders());
		logoutButton.setOnAction(event -> handleLogout());

//		 Set up menu actions
		logout.setOnAction(event -> handleLogout());
		viewProfile.setOnAction(event -> handleViewProfile());
	}

	private void updatePopularBooks(List<Book> popularBooks) {
		popularBooksList.getItems().clear();
		popularBooksList.getItems().addAll(popularBooks);
		popularBooksList.setTooltip(new Tooltip("Top 5 Books"));
		popularBooksList.setEditable(false);
		popularBooksList.setStyle("-fx-font-family: Consolas,SansSerif");
		popularBooksList.setStyle("-fx-font-size: 16px");
	}

	@FXML
	private void handleViewProfile() {
		showModalDialog("/view/edit.fxml", "View Profile");
	}

	// open edit profile window as a modal dialog
	@FXML
	private void handleEditProfile() {
		showModalDialog("/view/edit.fxml", "edit Profile");
	}

	// open cart window as a modal dialog
	@FXML
	private void handleViewCart() {
		showModalDialog("/view/CartView.fxml", "Your Cart");
	}

	// open bookstore window as a modal dialog
	@FXML
	private void handleViewBookStore() {
		showModalDialog("/view/Bookstore.fxml", "Bookstore");
	}

	@FXML
	private void handleViewOrders() {
		Main.changeScene("/view/orders.fxml", "Your Orders");
	}


	@FXML
	private void handleLogout() {
		model.setCurrentUser(null);
		stage.close();
		parentStage.close();
		try {
			model.setup();
			model.setup();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));

			// Customize controller instance
			Stage stage = new Stage();
			setupStage(loader, stage, model);

		} catch (IOException | SQLException | RuntimeException e) {
			e.printStackTrace();
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


	// Utility method to open modal dialog windows
	private void showModalDialog(String fxmlFile, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Pane root = loader.load();

			// Create a  modal dialog
			Stage modalStage = new Stage();
			modalStage.initModality(Modality.APPLICATION_MODAL);
			modalStage.initOwner(stage);
			modalStage.setTitle(title);
			Scene scene = new Scene(root);
			modalStage.setScene(scene);
			modalStage.setResizable(false);
			modalStage.centerOnScreen();
			modalStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
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
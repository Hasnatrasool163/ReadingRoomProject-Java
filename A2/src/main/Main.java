package main;


import java.io.IOException;
import java.sql.SQLException;

import controller.AdminDashboardController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;

import model.Model;

public class Main extends Application {
	private Model model;

//	private static Stage primaryStage;


	@Override
	public void init() {
		model = new Model();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			model.setup();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
			
			// Customize controller instance
			AdminDashboardController.setupStage(loader, primaryStage, model);

//			loginController.showStage(root);
		} catch (IOException | SQLException | RuntimeException e) {
			Scene scene = new Scene(new Label(e.getMessage()), 200, 100);
			primaryStage.setTitle("Error");
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.show();
		}
	}

	public static void changeScene(String fxmlFile, String title) {
		switchScene(fxmlFile, title);
	}

	public static void switchScene(String fxmlFile, String title) {
		Stage primaryStage = new Stage();
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
			Scene scene = new Scene(loader.load());
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

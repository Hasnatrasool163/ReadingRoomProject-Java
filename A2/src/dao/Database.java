package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	// URL pattern for database
	private static final String DB_URL = "jdbc:sqlite:application.db";

	public static Connection getConnection() throws SQLException {
		// DriverManager is the basic service for managing a set of JDBC drivers
		return DriverManager.getConnection(DB_URL);
	}
}

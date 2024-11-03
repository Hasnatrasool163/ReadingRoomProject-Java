package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.User;

public class UserDaoImpl implements UserDao {
	private final String TABLE_NAME = "users";

	public UserDaoImpl() {
	}

	@Override
	public void setup() throws SQLException {
		try (Connection connection = Database.getConnection();
				Statement stmt = connection.createStatement()) {
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
					"(user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+" username VARCHAR(20) NOT NULL UNIQUE,"
					+ "password VARCHAR(20) NOT NULL,"
					+"first_name VARCHAR(20) NOT NULL,"
					+ "last_name VARCHAR(20) NOT NULL,"
					+ "role VARCHAR(5) CHECK(role IN ('user', 'admin')))";
			stmt.executeUpdate(sql);
		} 
	}

	@Override
	public User getUser(String username, String password) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?";
		try (Connection connection = Database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("role")
                    );
				}
				return null;
			}
		}
	}

	@Override
	public User createUser(String username, String password, String firstName , String lastName , String role) throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + " (username, password, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = Database.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setString(5, role);

			stmt.executeUpdate();
			return new User(username, password,firstName,lastName,role);
		} 
	}

	@Override
	public void updateUser(User user) throws SQLException {
		String sql = "UPDATE users SET first_name = ?, last_name = ?, password = ? WHERE username = ?";
		try (Connection connection = Database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setString(3, user.getPassword());
			stmt.setString(4, user.getUsername());

			stmt.executeUpdate();
		}
	}

	public int getUserIdByUsername(String username) throws SQLException {
		String sql = "SELECT user_id FROM users WHERE username = ?";
		int userId = 0;
		try (Connection connection = Database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("user_id");
			}
		}
		return userId;
	}


}

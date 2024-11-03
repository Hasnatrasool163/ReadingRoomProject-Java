package dao;

import model.CartItem;
import model.ShoppingCart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartDaoImpl implements ShoppingCartDao{

    private final String TABLE_NAME = "shopping_cart";

    public ShoppingCartDaoImpl() {
    }

    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "cart_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER NOT NULL,"
                    + "book_id INTEGER NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "FOREIGN KEY(user_id) REFERENCES users(user_id),"
                    + "FOREIGN KEY(book_id) REFERENCES books(book_id),"
                    + "UNIQUE(user_id, book_id))";
            stmt.executeUpdate(sql);
        }
    }

    // Add or update item in shopping cart
    @Override
    public void addOrUpdateCartItem(ShoppingCart cartItem) throws SQLException {
        int userId = cartItem.getUserId();
        int bookId = cartItem.getBookId();
        int quantity = cartItem.getQuantity();
        String sql = "INSERT INTO " + TABLE_NAME + " (user_id, book_id, quantity) VALUES (?, ?, ?) "
                + "ON CONFLICT(user_id, book_id) DO UPDATE SET quantity = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
        }
    }


    public void addItem(ShoppingCart cartItem) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (user_id, book_id, quantity) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartItem.getUserId());
            stmt.setInt(2, cartItem.getBookId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.executeUpdate();
        }
    }

    // Remove an item from the cart
    @Override
    public void removeCartItem(int userId, int bookId) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE user_id = ? AND book_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    // Get all cart items for a specific user
    @Override
    public List<ShoppingCart> getCartItemsByUser(String userName) throws SQLException {
        int id = getUserId(userName);
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";
        List<ShoppingCart> cartItems = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShoppingCart cartItem = new ShoppingCart(
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getInt("quantity")
                );
                cartItems.add(cartItem);
            }
        }
        return cartItems;
    }

    public int getUserId(String userName) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        int id = 0;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("user_id");
            }
        }
        return id;
    }

    // Clear all items in the user's cart
    @Override
    public void clearCartForUser(int userId) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE user_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public List<CartItem> getCartItemsByUserName(String userName) throws SQLException {
        int id = getUserId(userName);
        String sql = "SELECT b.title, b.price, sc.quantity " +
                "FROM " + TABLE_NAME + " sc " +
                "JOIN books b ON sc.book_id = b.book_id " +
                "WHERE sc.user_id = ?";
        List<CartItem> cartItems = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                CartItem shoppingCart = new CartItem(title, price, quantity);
                cartItems.add(shoppingCart);
            }
        }
        return cartItems;
    }
}

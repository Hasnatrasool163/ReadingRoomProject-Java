package dao;

import model.Order;
import model.OrderDetails;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao{

    private final String TABLE_NAME = "orders";

    public OrderDaoImpl() {
    }

    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "order_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_id INTEGER NOT NULL,"
                    + "total_price REAL NOT NULL,"
                    + "order_date TEXT NOT NULL,"
                    + "FOREIGN KEY(user_id) REFERENCES users(user_id)"
                    + ")";
            stmt.executeUpdate(sql);
        }
    }

    // Add new order
    public void addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (user_id, total_price, order_date) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getUser_id());
            stmt.setDouble(2, order.getTotalPrice());
            stmt.setString(3, order.getDateTime().toString());
            stmt.executeUpdate();
        }
    }

    // Fetch all orders
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("order_date")),
                        rs.getDouble("total_price")
                );
                orders.add(order);
            }
        }
        return orders;
    }

    // Fetch orders for a specific user
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("order_date")),
                        rs.getDouble("total_price")
                );
                orders.add(order);
            }
        }
        return orders;
    }

    // Fetch orders using userName
    public List<Order> getOrdersByUserName(String userName) throws SQLException {
        int userId = getUserId(userName); // Get the user_id using username
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("order_date")),
                        rs.getDouble("total_price")
                );
                orders.add(order);
            }
        }
        return orders;
    }

    public List<Order> getOrdersWithDetailsByUserName(String userName) throws SQLException {
        int userId = getUserId(userName);  // Get user_id by username
        String sql = "SELECT o.order_id, o.order_date, o.total_price, b.book_id, b.title, od.quantity, od.price " +
                "FROM orders o " +
                "JOIN order_details od ON o.order_id = od.order_id " +
                "JOIN books b ON od.book_id = b.book_id " +
                "WHERE o.user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                LocalDateTime orderDate = LocalDateTime.parse(rs.getString("order_date"));
                double totalPrice = rs.getDouble("total_price");
                int bookId = rs.getInt("book_id");
                String bookTitle = rs.getString("title");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                // Create order details
                OrderDetails orderDetails = new OrderDetails(orderId, bookId, quantity, price, bookTitle);
                Order existingOrder = orders.stream()
                        .filter(o -> o.getOrderId() == orderId)
                        .findFirst()
                        .orElse(null);

                if (existingOrder == null) {
                    Order order = new Order(orderId, userId, orderDate, totalPrice);
                    order.setOrderDetailsList(new ArrayList<>());
                    order.getOrderDetailsList().add(orderDetails);
                    orders.add(order);
                } else {
                    existingOrder.getOrderDetailsList().add(orderDetails);
                }
            }
        }
        return orders;
    }



    private int getUserId(String userName) throws SQLException {
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

    public int getLatestOrderId(int userId) throws SQLException {
        String sql = "SELECT order_id FROM orders WHERE user_id = ? ORDER BY order_date DESC LIMIT 1";
        int orderId = 0;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                orderId = rs.getInt("order_id");
            }
        }
        return orderId;
    }

}


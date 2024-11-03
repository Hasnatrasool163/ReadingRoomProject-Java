package dao;

import model.Order;
import model.OrderDetails;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDaoImpl {
    private final String TABLE_NAME = "order_details";

    public OrderDetailsDaoImpl() {
    }

    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "order_detail_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "order_id INTEGER NOT NULL,"
                    + "book_id INTEGER NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "FOREIGN KEY(order_id) REFERENCES orders(order_id),"
                    + "FOREIGN KEY(book_id) REFERENCES books(book_id)"
                    + ")";
            stmt.executeUpdate(sql);
        }
    }

    // Add order detail
    public void addOrderDetail(OrderDetails orderDetail) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderDetail.getOrderId());
            stmt.setInt(2, orderDetail.getBookId());
            stmt.setInt(3, orderDetail.getQuantity());
            stmt.setDouble(4, orderDetail.getPrice());
            stmt.executeUpdate();
        }
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

                // Create OrderDetails
                OrderDetails orderDetails = new OrderDetails(orderId, bookId, quantity, price, bookTitle);

                // Check if the order already exists in the list
                Order existingOrder = orders.stream()
                        .filter(o -> o.getOrderId() == orderId)
                        .findFirst()
                        .orElse(null);

                if (existingOrder == null) {
                    // Create new order
                    Order order = new Order(userId, orderDate, totalPrice);
                    order.setOrderDetailsList(new ArrayList<>());
                    order.getOrderDetailsList().add(orderDetails);
                    orders.add(order);
                } else {
                    // Add to existing order
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

}

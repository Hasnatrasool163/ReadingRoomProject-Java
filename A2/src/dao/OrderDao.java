package dao;

import model.Order;

import java.sql.SQLException;
import java.util.List;

/**
 * A data access object (DAO) is a pattern that provides an abstract interface
 * to a database or other persistence mechanism.
 * the DAO maps application calls to the persistence layer and provides some specific data operations
 * without exposing details of the database.
 */
public interface OrderDao {
    void setup() throws SQLException;
    void addOrder(Order order) throws SQLException;
    List<Order> getAllOrders() throws SQLException;
    List<Order> getOrdersByUserId(int userId) throws SQLException;
    List<Order> getOrdersByUserName(String userName) throws SQLException;
    List<Order> getOrdersWithDetailsByUserName(String userName) throws SQLException;
    int getLatestOrderId(int userId) throws SQLException;
}
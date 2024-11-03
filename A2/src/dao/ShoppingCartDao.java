package dao;


import model.ShoppingCart;

import java.sql.SQLException;
import java.util.List;


/**
 * A data access object (DAO) is a pattern that provides an abstract interface
 * to a database or other persistence mechanism.
 * the DAO maps application calls to the persistence layer and provides some specific data operations
 * without exposing details of the database.
 */
public interface ShoppingCartDao {
     void setup() throws SQLException;
     void addOrUpdateCartItem(ShoppingCart cartItem) throws SQLException;
     void removeCartItem(int userId, int bookId) throws SQLException;
     List<ShoppingCart> getCartItemsByUser(String username) throws SQLException;
     void clearCartForUser(int userId) throws SQLException;
}

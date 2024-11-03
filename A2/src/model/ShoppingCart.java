package model;

public class ShoppingCart {
    private int cartId;
    private int userId;
    private int bookId;
    private int quantity;

    // Constructor for ShoppingCart
    public ShoppingCart( int userId, int bookId, int quantity) {
        this.userId = userId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters
    public int getCartId() {
        return cartId;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}

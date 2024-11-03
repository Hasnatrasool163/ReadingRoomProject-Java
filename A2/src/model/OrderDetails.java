package model;

public class OrderDetails {
    private int orderId;
    private int bookId;
    private int quantity;
    private double price;
    private String title;

    // Constructor
    public OrderDetails(int orderId, int bookId, int quantity, double price , String bookTitle) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.title=bookTitle;
    }

    // Getter
    public int getOrderId() {
        return orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }
}

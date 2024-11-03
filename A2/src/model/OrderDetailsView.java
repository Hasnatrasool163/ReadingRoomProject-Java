package model;

public class OrderDetailsView {
    private int orderId;
    private String orderDate;
    private String bookTitle;
    private int quantity;
    private double price;
    private double totalPrice;

    // Constructor
    public OrderDetailsView(int orderId, String orderDate, String bookTitle, int quantity, double totalPrice) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

package model;

public class CartItem {
    private String title;
    private double price;
    private int quantity;
    private double totalPrice;

    // Constructor
    public CartItem(String title,double price,  int quantity) {
        this.title=title;
        this.price=price;
        this.quantity = quantity;
    }

    // Getter methods
    public String getTitle() {return title;}

    public double getPrice() {return price;}

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {return quantity * price;}
}

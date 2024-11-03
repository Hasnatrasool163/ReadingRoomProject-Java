package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order {
    private int orderId;
    private int user_id;
    private LocalDateTime dateTime;
    private double totalPrice;
    private List<OrderDetails> orderDetailsList;

    // Constructor with order details list
    public Order(int user_id, LocalDateTime dateTime, double totalPrice, List<OrderDetails> orderDetailsList) {
        this.user_id = user_id;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.orderDetailsList = orderDetailsList;
    }

    public Order(int user_id,  LocalDateTime dateTime, double totalPrice) {
        this.user_id=user_id;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }

    public Order(int orderId, int user_id,  LocalDateTime dateTime, double totalPrice) {
        this.orderId=orderId;
        this.user_id=user_id;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
    }


    // Getters
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUser_id() {
        return user_id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<OrderDetails> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<OrderDetails> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

}

package model;

public class Book {
    // Book stores values of title, author, physicalCopies, and hasEbook
    private String title;
    private String author;
    private int physicalCopies;
    private double price;
    private int soldCopies;
    private boolean hasEbook;

    // Constructor for Book
    public Book(String title, String author,  int physicalCopies, double price, int soldCopies ,boolean hasEbook) {
        this.title = title;
        this.author = author;
        this.physicalCopies = physicalCopies;
        this.price=price;
        this.soldCopies=soldCopies;
        this.hasEbook = hasEbook;
    }

    // Getters for Book title
    public String getTitle() {
        return title;
    }

    // Getters for Book author
    public String getAuthor() {
        return author;
    }

    // Getters for Book author
    public double getPrice() {return price;}

    // Getters for Book author
    public int getSoldCopies() {return soldCopies;}

    // Getters for Book physicalCopies
    public int getPhysicalCopies() {
        return physicalCopies;
    }

    // Getters for Book hasEbook
    public boolean hasEbook() {
        return hasEbook;
    }

    // Method to reduce physicalCopies
    public void reduceCopies() {
        if (physicalCopies > 0) {
            physicalCopies--;
        }
    }

    // Method to reduce physicalCopies
    public void reduceCopies(int quantity) {
        if (physicalCopies > quantity) {
            physicalCopies-=quantity;
        }
    }

    // Setters for updating stock and sold copies
    public void setPhysicalCopies(int physicalCopies) { this.physicalCopies = physicalCopies; }
    public void setSoldCopies(int soldCopies) { this.soldCopies = soldCopies; }

    // Method to increase physicalCopies
    public void increaseCopies() {
        physicalCopies++;
    }

    // Override toString method to return the title, author, physicalCopies, and hasEbook
    // Printing the book details in the format: title | author | physicalCopies copies | hasEbook
    @Override
    public String toString() {
        return(this.getTitle() + " | " + this.getAuthor() + " | " +this.price + "|"
                            + this.getPhysicalCopies() + " copies | " +  this.getSoldCopies() + " copies | "+ (this.hasEbook() ? "yes" : "no"));
         
    }
}

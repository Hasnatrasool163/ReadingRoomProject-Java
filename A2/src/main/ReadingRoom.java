package main;

import model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import static main.BookPricing.EBOOK_PRICE;

// Book class to represent a book price for ebook and physical copy
interface BookPricing {
    double EBOOK_PRICE = 8.00;
    double PHYSICAL_BOOK_PRICE = 50.00;
}

// main.ReadingRoom class to represent a reading room with books
public class ReadingRoom {
    private ArrayList<Book> books;
    private Map<Book, Integer> shoppingCart; // Use a Map to store the book and quantity
    private Scanner scanner;

    // Constructor
    public ReadingRoom() {
        scanner = new Scanner(System.in);
        books = new ArrayList<>();
        shoppingCart = new HashMap<>();
        initializeBooks();
    }

    // Initialize the default books
    private void initializeBooks() {
        books.add(new Book("Absolute Java", "Savitch", 5, EBOOK_PRICE ,0,true));
        books.add(new Book("JAVA: How to Program", "Deitel and Deitel", 0, EBOOK_PRICE,0,true));
        books.add(new Book("Computing Concepts with JAVA 8 Essentials", "Horstman", 5, EBOOK_PRICE,0,false));
        books.add(new Book("Java Software Solutions", "Lewis and Loftus", 5, EBOOK_PRICE,0,false));
        books.add(new Book("Java Program Design", "Cohoon and Davidson", 1, EBOOK_PRICE,0,true));
    }

    // Start the program 
    public void start() {
        // Print welcome message
        System.out.println("\n===============================================");
        System.out.println("         Welcome to The Reading Room!");
        System.out.println("===============================================");

        while (true) {
            printMenu();
            int choice = getIntInput("Choose an option: ");
            handleMenuSelection(choice);
        }
    }

    // Print menu options
    private void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Add a book to shopping cart");
        System.out.println("2. View shopping cart");
        System.out.println("3. Remove a book from shopping cart");
        System.out.println("4. Checkout");
        System.out.println("5. List all books");
        System.out.println("6. Quit\n");
    }

    // Handle menu selection based on user input
    private void handleMenuSelection(int choice) {
        switch (choice) {
            case 1:
                addToCart();
                break;
            case 2:
                viewCart();
                break;
            case 3:
                removeFromCart();
                break;
            case 4:
                checkout();
                break;
            case 5:
                listAllBooks();
                break;
            case 6:
                quit();
                break;
            default:
                System.out.println("Invalid option! Please select a valid option.");
                break;
        }
    }

    // Add a book to the shopping cart using a keyword search
    private void addToCart() {
        String keyword = getStringInput("Enter a keyword: ");
        ArrayList<Book> matchingBooks = new ArrayList<>();

        // Search for books that match the keyword
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                matchingBooks.add(book);
            }
        }

        // If no books found, inform the user
        if (matchingBooks.isEmpty()) {
            System.out.println("No books found matching the keyword: " + keyword);
            return;
        }

        // Display matching books
        System.out.println("The following books are found:");
        for (int i = 0; i < matchingBooks.size(); i++) {
            System.out.println((i + 1) + ". " + matchingBooks.get(i).getTitle());
        }
        System.out.println((matchingBooks.size() + 1) + ". Cancel");

        // Prompt the user to select a book or cancel
        int selection = getIntInput("Please select: ");
        if (selection <= 0 || selection > matchingBooks.size()) {
            System.out.println("Cancelled or invalid selection.");
            return;
        }

        Book selectedBook = matchingBooks.get(selection - 1);
        boolean isEbook = getBooleanInput("Do you want to buy this as an ebook (yes/no): ");
        int quantity = getIntInput("How many copies do you want to buy: ");
        
        // Create a new book instance to represent the selected format
        Book bookToAdd = new Book(selectedBook.getTitle(), selectedBook.getAuthor(), 
                                selectedBook.getPhysicalCopies(),EBOOK_PRICE,1, isEbook);

        // Check if the book already exists in the cart
        for (Map.Entry<Book, Integer> entry : shoppingCart.entrySet()) {
            Book existingBook = entry.getKey();
            if (existingBook.getTitle().equals(bookToAdd.getTitle()) && existingBook.hasEbook() == bookToAdd.hasEbook()) {
                // Update the quantity if the book exists
                shoppingCart.put(existingBook, entry.getValue() + quantity);
                System.out.println("\"" + existingBook.getTitle() + "\" has been updated in your cart.");
                return;
            }
        }

        // Add the new book to the cart if it does not exist on cart
        shoppingCart.put(bookToAdd, quantity);
        // Reduce the physical copies of the book in the store
        if (!isEbook) {
            reducePhysicalCopies(selectedBook, quantity);
        }
        System.out.println("\"" + selectedBook.getTitle() + "\" has been added to your cart.");
    }

    // View the shopping cart with correct formatting
    private void viewCart() {
        if (shoppingCart.isEmpty()) {
            System.out.println("Your shopping cart is empty.");
        } else {
            System.out.println("Your shopping cart contains the following book(s):");
            int index = 1;
            for (Map.Entry<Book, Integer> entry : shoppingCart.entrySet()) {
                Book book = entry.getKey();
                int quantity = entry.getValue();
                String bookType = book.hasEbook() ? "e-book" : "physical copy";
                
                // Display in the required format: Title | Quantity copies | Type
                System.out.println(index + ". " + book.getTitle() + " | " 
                                                + quantity + (quantity > 1 ? " copies" : " copy") + " | "
                                                + bookType);
                index++;
            }
        }
    }

    // Remove a book from the shopping cart
// Remove a book from the shopping cart
    private void removeFromCart() {
        viewCart();
        int cartIndex = getIntInput("Select the item number to remove from the cart: ") - 1;

        if (cartIndex >= 0 && cartIndex < shoppingCart.size()) {
            ArrayList<Book> cartBooks = new ArrayList<>(shoppingCart.keySet());
            Book bookToRemove = cartBooks.get(cartIndex);
            int quantity = shoppingCart.get(bookToRemove); // Get the quantity before removing
            shoppingCart.remove(bookToRemove); // Remove the book from the cart
            // Increase the physical copies of the book in the store
            if (!bookToRemove.hasEbook()) {
                increasePhysicalCopies(bookToRemove, quantity);
            }
            System.out.println("Removed " + bookToRemove.getTitle() + " from your cart.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // Checkout the books in the shopping cart
    private void checkout() {
        double totalPrice = 0;
        for (Map.Entry<Book, Integer> entry : shoppingCart.entrySet()) {
            Book book = entry.getKey();
            int quantity = entry.getValue();
            totalPrice += (book.hasEbook() ? EBOOK_PRICE : BookPricing.PHYSICAL_BOOK_PRICE) * quantity;
        }
        System.out.println("Total: $" + totalPrice);        
        shoppingCart.clear();
        System.out.println("Thanks for shopping with The Reading Room!");
    }

    // Reduce the physical copies of the book in the store when added to cart  
    private void reducePhysicalCopies(Book book, int quantity) {
        for (Book b : books) {
            if (b.getTitle().equals(book.getTitle())) {
                for (int i = 0; i < quantity; i++) {
                    b.reduceCopies();
                }
            }
        }
    }

    // Increase the physical copies of the book in the store when removed from cart
    private void increasePhysicalCopies(Book book, int quantity) {
        for (Book b : books) {
            if (b.getTitle().equals(book.getTitle())) {
                for (int i = 0; i < quantity; i++) {
                    b.increaseCopies();
                }
            }
        }
    }

    // List all available books
    private void listAllBooks() {
        System.out.println("The following books are available:");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    // Quit the program
    private void quit() {
        System.out.println("Goodbye!");
        scanner.close();
        System.exit(0);
    }

    // Get integer input from the user
    private int getIntInput(String prompt) {
        int choice = -1;
        while (true) {
            System.out.print(prompt);
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
            }
        }
        return choice;
    }

    // Get boolean input from the user
    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    // Get string input from the user
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // main.Main method to start the program
    public static void main(String[] args) {
        main.ReadingRoom readingRoom = new main.ReadingRoom(); // Create a new main.ReadingRoom instance
        readingRoom.start(); // Start the reading room instance/program
    }
}

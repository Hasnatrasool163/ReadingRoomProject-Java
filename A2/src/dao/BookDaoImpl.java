package dao;

import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    private final String TABLE_NAME = "books";

    public BookDaoImpl() {
    }

    @Override
    public void setup() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "book_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title VARCHAR(100) NOT NULL,"
                    + "author VARCHAR(100) NOT NULL,"
                    + "price DOUBLE NOT NULL,"
                    + "physical_copies INT NOT NULL,"
                    + "sold_copies INT NOT NULL DEFAULT 0,"
                    + "e_book BOOLEAN DEFAULT FALSE)";
            stmt.executeUpdate(createTable);
            //            getAllBooks();
        }
    }

    public void insertInitialBooks() throws SQLException {
        addBook(new Book("Absolute Java", "Savitch", 10, 50.0, 142, false));
        addBook(new Book("JAVA: How to Program", "Deitel and Deitel", 100, 70.0, 475, false));
        addBook(new Book("Computing Concepts with JAVA 8 Essentials", "Horstman", 500, 89.0, 60, false));
        addBook(new Book("Java Software Solutions", "Lewis and Loftus", 500, 99.0, 12, false));
        addBook(new Book("Java Program Design", "Cohoon and Davidson", 2, 29.0, 86, false));
        addBook(new Book("Clean Code", "Robert Martin", 100, 45.0, 300, false));
        addBook(new Book("Gray Hat C#", "Brandon Perry", 300, 68.0, 178, false));
        addBook(new Book("Python Basics", "David Amos", 1000, 49.0, 79, false));
        addBook(new Book("Bayesian Statistics The Fun Way", "Will Kurt", 600, 42.0, 155, false));

    }

    @Override
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (title, author, price, physical_copies, sold_copies , e_book) VALUES (?, ?, ?, ?, ? ,?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getPhysicalCopies());
            stmt.setInt(5, book.getSoldCopies());
            stmt.setBoolean(6, book.hasEbook());
            stmt.executeUpdate();
        }
    }


    public void deleteBook(Book book) throws SQLException {
        String sql = "DELETE  from "+TABLE_NAME+" where title=" +book.getTitle();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.executeUpdate();
        }
    }

    public void deleteBook(String title) throws SQLException {
        String sql = "DELETE FROM "+TABLE_NAME+" WHERE title = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<Book> books = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("physical_copies"),
                        rs.getDouble("price"),
                        rs.getInt("sold_copies"),
                        rs.getBoolean("e_book")
                );
//                System.out.println("Loaded book: " + book.getTitle()); // log
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public void updateStock(String title, int newStock) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET physical_copies = ? WHERE title = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setString(2, title);
            stmt.executeUpdate();
        }
    }

    public List<Book> getTop5Books() throws SQLException {
        List<Book> books = getAllBooks();  // Get all books
        // Sort books by sold copies in descending order
        books.sort((b1, b2) -> Integer.compare(b2.getSoldCopies(), b1.getSoldCopies()));
        return books.subList(0, Math.min(5, books.size()));
    }

    public int getBookIdByTitle(String title) throws SQLException {
        String sql = "SELECT book_id FROM books WHERE title = ?";
        int bookId = 0;
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bookId = rs.getInt("book_id");
            }
        }
        return bookId;
    }

}

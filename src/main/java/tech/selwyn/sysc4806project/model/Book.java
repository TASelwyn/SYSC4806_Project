package tech.selwyn.sysc4806project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Represents a book in the Amazin Online Bookstore
 * Stores catalog information, pricing, and inventory details
 * Each instance corresponds to a record in the books table
 */

@Entity
@Table(name = "books")
public class Book {

    // Unique database identifier generated automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String publisher;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    // BigDecimal avoids floating-point precision issues with prices.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    /**
     * Default constructor
     */
    protected Book() {
    }

    /**
     * Constructs a book with its catalog and inventory information
     * The database generates the ID when the book is persisted.
     */
    public Book(String isbn, String title, String author,
                String publisher, String description,
                String imageUrl, BigDecimal price,
                int stockQuantity) {

        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}


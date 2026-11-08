package tech.selwyn.sysc4806project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a book in the Amazin Online Bookstore
 * Stores catalog information, pricing, and inventory details
 * Each instance corresponds to a record in the books table
 */

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {

    // Unique database identifier generated automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
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

}


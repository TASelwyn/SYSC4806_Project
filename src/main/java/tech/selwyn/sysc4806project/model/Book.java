package tech.selwyn.sysc4806project.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Represents a book in the Amazin Online Bookstore
 * Stores catalog information, pricing, and inventory details
 * Each instance corresponds to a record in the books table
 */

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Book {

    // Unique database identifier generated automatically.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, unique = true, length = 13)
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @PositiveOrZero
    private int stockQuantity;


}


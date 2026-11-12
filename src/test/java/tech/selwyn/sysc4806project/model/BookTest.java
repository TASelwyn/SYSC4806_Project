
package tech.selwyn.sysc4806project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

/**
 * Unit tests for the Book entity.
 */
class BookTest {

    /**
     * Verify that Lombok's builder correctly initializes
     * all book attributes.
     */
    @Test
    void testBookBuilder() {

        Book book = Book.builder()
                .isbn("9780134685991")
                .title("Effective Java")
                .author("Joshua Bloch")
                .publisher("Addison-Wesley")
                .description("A guide to effective Java programming.")
                .imageUrl("https://example.com/book.jpg")
                .price(new BigDecimal("49.99"))
                .stockQuantity(10)
                .build();

        // ID should be null before database persistence.
        Assertions.assertNull(book.getId());

        // Verify the builder assigned each field correctly
        Assertions.assertEquals("9780134685991", book.getIsbn());
        Assertions.assertEquals("Effective Java", book.getTitle());
        Assertions.assertEquals("Joshua Bloch", book.getAuthor());
        Assertions.assertEquals("Addison-Wesley", book.getPublisher());
        Assertions.assertEquals("A guide to effective Java programming.",
                book.getDescription());
        Assertions.assertEquals("https://example.com/book.jpg",
                book.getImageUrl());
        Assertions.assertEquals(new BigDecimal("49.99"), book.getPrice());
        Assertions.assertEquals(10, book.getStockQuantity());
    }

    /**
     * Verify that the JPA-compatible no-argument
     * constructor and Lombok setters function correctly.
     */
    @Test
    void testNoArgsConstructorAndSetters() {

        Book book = new Book();

        // Populate the entity using Lombok-generated setters
        book.setIsbn("9780132350884");
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setPublisher("Prentice Hall");
        book.setPrice(new BigDecimal("39.99"));
        book.setStockQuantity(5);

        // Verify the values through Lombok-generated getters.
        Assertions.assertEquals("9780132350884", book.getIsbn());
        Assertions.assertEquals("Clean Code", book.getTitle());
        Assertions.assertEquals("Robert C. Martin", book.getAuthor());
        Assertions.assertEquals("Prentice Hall", book.getPublisher());
        Assertions.assertEquals(new BigDecimal("39.99"), book.getPrice());
        Assertions.assertEquals(5, book.getStockQuantity());
    }

    /**
     * Check the JPA mapping and ISBN database
     * constraints declared on the Book entity.
     */
    @Test
    void testJpaAnnotations() throws NoSuchFieldException {

        // Book must be recognized as a JPA entity
        Assertions.assertTrue(Book.class.isAnnotationPresent(Entity.class));

        // Verify the database table name
        Table table = Book.class.getAnnotation(Table.class);
        Assertions.assertNotNull(table);
        Assertions.assertEquals("books", table.name());

        // Verify that ID is the generated primary key
        var idField = Book.class.getDeclaredField("id");

        Assertions.assertTrue(idField.isAnnotationPresent(Id.class));
        Assertions.assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        // Verify ISBN column constraints.
        var isbnField = Book.class.getDeclaredField("isbn");
        Column isbnColumn = isbnField.getAnnotation(Column.class);

        Assertions.assertNotNull(isbnColumn);
        Assertions.assertEquals(13, isbnColumn.length());
        Assertions.assertTrue(isbnColumn.unique());
        Assertions.assertFalse(isbnColumn.nullable());
    }
}

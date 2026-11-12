
package tech.selwyn.sysc4806project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import tech.selwyn.sysc4806project.model.Book;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNull(book.getId());

        // Verify the builder assigned each field correctly
        assertEquals("9780134685991", book.getIsbn());
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("Addison-Wesley", book.getPublisher());
        assertEquals("A guide to effective Java programming.",
                book.getDescription());
        assertEquals("https://example.com/book.jpg",
                book.getImageUrl());
        assertEquals(new BigDecimal("49.99"), book.getPrice());
        assertEquals(10, book.getStockQuantity());
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
        assertEquals("9780132350884", book.getIsbn());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals("Prentice Hall", book.getPublisher());
        assertEquals(new BigDecimal("39.99"), book.getPrice());
        assertEquals(5, book.getStockQuantity());
    }

    /**
     * Check the JPA mapping and ISBN database
     * constraints declared on the Book entity.
     */
    @Test
    void testJpaAnnotations() throws NoSuchFieldException {

        // Book must be recognized as a JPA entity
        assertTrue(Book.class.isAnnotationPresent(Entity.class));

        // Verify the database table name
        Table table = Book.class.getAnnotation(Table.class);
        assertNotNull(table);
        assertEquals("books", table.name());

        // Verify that ID is the generated primary key
        var idField = Book.class.getDeclaredField("id");

        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        // Verify ISBN column constraints.
        var isbnField = Book.class.getDeclaredField("isbn");
        Column isbnColumn = isbnField.getAnnotation(Column.class);

        assertNotNull(isbnColumn);
        assertEquals(13, isbnColumn.length());
        assertTrue(isbnColumn.unique());
        assertFalse(isbnColumn.nullable());
    }
}


package tech.selwyn.sysc4806project.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import tech.selwyn.sysc4806project.model.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the BookRepository.
 */

@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Creates a sample Book for use in repository tests.
     */
    private Book createBook(String isbn, String title,
                            String author, String price) {

        return Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher("Test Publisher")
                .description("Sample book for testing.")
                .price(new BigDecimal(price))
                .stockQuantity(10)
                .build();
    }

    /**
     * Chek that a book can be saved to the database
     * and retrieved using its generated ID.
     */
    @Test
    void testSaveAndFindBook() {

        Book book = createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99");

        Book saved = bookRepository.saveAndFlush(book);

        // The database should generate a unique ID
        assertNotNull(saved.getId());

        // Clear cached entities to force a database retrieval.
        entityManager.clear();

        Book retrieved = bookRepository.findById(saved.getId())
                .orElseThrow();

        assertEquals(book.getIsbn(), retrieved.getIsbn());
        assertEquals("Effective Java", retrieved.getTitle());
        assertEquals("Joshua Bloch", retrieved.getAuthor());
        assertEquals(0, new BigDecimal("49.99")
                .compareTo(retrieved.getPrice()));
    }

    /**
     * Verify that findAll retrieves all stored books.
     */
    @Test
    void testFindAllBooks() {

        bookRepository.save(createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99"));

        bookRepository.save(createBook(
                "9780132350884", "Clean Code",
                "Robert C. Martin", "39.99"));

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
    }

    /**
     * Check that searching for a nonexistent book ID
     * returns an empty Optional.
     */
    @Test
    void testFindNonexistentBook() {

        assertTrue(bookRepository.findById(UUID.randomUUID())
                .isEmpty());
    }

    /**
     * case-insensitive partial title searching
     */
    @Test
    void testSearchByTitle() {

        bookRepository.save(createBook(
                "9780132350884", "Clean Code",
                "Robert C. Martin", "39.99"));

        List<Book> results =
                bookRepository.findByTitleContainingIgnoreCase("clean");

        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    /**
     * case-insensitive partial author searching.
     */
    @Test
    void testSearchByAuthor() {

        bookRepository.save(createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99"));

        List<Book> results =
                bookRepository.findByAuthorContainingIgnoreCase("bloch");

        assertEquals(1, results.size());
        assertEquals("Joshua Bloch", results.get(0).getAuthor());
    }

    /**
     * searching by title OR author
     * returns books matching either condition
     */
    @Test
    void testSearchByTitleOrAuthor() {

        bookRepository.save(createBook(
                "9780134685991", "Java Programming",
                "Joshua Bloch", "49.99"));

        bookRepository.save(createBook(
                "9780132350884", "Clean Code",
                "Java Expert", "39.99"));

        List<Book> results = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                        "java", "java");

        assertEquals(2, results.size());
    }

    /**
     * updating a book persists the new
     * price and inventory quantity in the database.
     */
    @Test
    void testUpdateBook() {

        Book saved = bookRepository.saveAndFlush(createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99"));

        UUID bookId = saved.getId();

        saved.setPrice(new BigDecimal("59.99"));
        saved.setStockQuantity(5);

        bookRepository.saveAndFlush(saved);

        // Force retrieval from the database.
        entityManager.clear();

        Book updated = bookRepository.findById(bookId)
                .orElseThrow();

        assertEquals(0, new BigDecimal("59.99")
                .compareTo(updated.getPrice()));
        assertEquals(5, updated.getStockQuantity());
    }

    /**
     * deleting a book removes its record
     */
    @Test
    void testDeleteBook() {

        Book saved = bookRepository.saveAndFlush(createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99"));

        UUID bookId = saved.getId();

        bookRepository.deleteById(bookId);
        bookRepository.flush();
        entityManager.clear();

        assertFalse(bookRepository.existsById(bookId));
    }

    /**
     * database rejects duplicate ISBNs
     * because the ISBN column has a unique constraint
     */
    @Test
    void testDuplicateIsbn() {

        bookRepository.saveAndFlush(createBook(
                "9780134685991", "Effective Java",
                "Joshua Bloch", "49.99"));

        Book duplicate = createBook(
                "9780134685991", "Another Java Book",
                "Another Author", "29.99");

        assertThrows(DataIntegrityViolationException.class,
                () -> bookRepository.saveAndFlush(duplicate));
    }
}


package tech.selwyn.sysc4806project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.selwyn.sysc4806project.model.Book;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

import java.util.List;

/**
 * Repository for managing and searching bookstore records
 * Standard CRUD operations are provided by Spring Data JPA
 */
public interface BookRepository extends JpaRepository<Book, UUID> {

    /**
     * Finds books by partial title, ignoring case
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds books by partial author name, ignoring case
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Finds books matching a title or author search
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author);

    /**
     * Finds books that contain specified ISBN.
     */
    List<Book> findByIsbnContainingIgnoreCase(String isbn);

    /**
     * Searches books by title, author, or ISBN and can optionally filter
     * for books in stock.
     */
    @Query("""
        SELECT b FROM Book b
        WHERE (
            :search IS NULL
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (:inStockOnly = false OR b.stockQuantity > 0)
    """)
    List<Book> searchCatalog(
            @Param("search") String search,
            @Param("inStockOnly") boolean inStockOnly,
            Sort sort
    );
}

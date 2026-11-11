
package tech.selwyn.sysc4806project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.selwyn.sysc4806project.model.Book;

import java.util.List;

/**
 * Repository for managing and searching bookstore records
 * Standard CRUD operations are provided by Spring Data JPA
 */
public interface BookRepository extends JpaRepository<Book, Long> {

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
     * Finds books that contain specified ISBN
     */
    List<Book> findByIsbnContainingIgnoreCase(String isbn);
}

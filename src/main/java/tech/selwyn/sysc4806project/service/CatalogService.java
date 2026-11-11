package tech.selwyn.sysc4806project.service;

import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.model.Book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;

public class CatalogService {
    private final BookRepository bookRepository;

    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Returns all books in the repository
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Returns a book by its ID
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> searchBooks(String search, boolean inStockOnly, boolean sortByPrice) {
        Sort sort = sortByPrice ? Sort.by(Sort.Direction.ASC, "price") : Sort.unsorted();

        return bookRepository.searchCatalog(search, inStockOnly, sort);
    }
}

package tech.selwyn.sysc4806project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.repository.BookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void getAllBooksReturnsAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = catalogService.getAllBooks();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository).findAll();
    }

    @Test
    void getBookByIdReturnsBook() {
        Book book = Book.builder()
                .isbn("1234567890123")
                .title("Testing Book")
                .author("Testing Author")
                .publisher("Testing Publisher")
                .description("Testing Description")
                .price(new BigDecimal("10.00"))
                .stockQuantity(5)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = catalogService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void getBookByIdReturnsEmptyWhenNotFound() {
        when(bookRepository.findById(123456789L)).thenReturn(Optional.empty());

        Optional<Book> result = catalogService.getBookById(123456789L);

        assertTrue(result.isEmpty());
    }
}

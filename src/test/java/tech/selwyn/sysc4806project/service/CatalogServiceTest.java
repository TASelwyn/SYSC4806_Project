package tech.selwyn.sysc4806project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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

    @Test
    void searchBooksWithoutFiltersOrSorting() {
        List<Book> expectedBooks = List.of(
                Book.builder()
                        .isbn("1234567890123")
                        .title("Testing Book")
                        .author("Testing Author")
                        .publisher("Testing Publisher")
                        .price(new BigDecimal("10.00"))
                        .stockQuantity(5)
                        .build()
        );

        when(bookRepository.searchCatalog(
                "Testing",
                false,
                Sort.unsorted()
        )).thenReturn(expectedBooks);

        List<Book> result = catalogService.searchBooks("Testing", false, false);

        assertEquals(expectedBooks, result);

        verify(bookRepository).searchCatalog("Testing", false, Sort.unsorted());
    }

    @Test
    void searchBooksWithInStockFiltering() {
        List<Book> expectedBooks = List.of(mock(Book.class));

        when(bookRepository.searchCatalog("Java", true, Sort.unsorted())).thenReturn(expectedBooks);

        List<Book> result = catalogService.searchBooks("Java", true, false);

        assertEquals(expectedBooks, result);

        verify(bookRepository).searchCatalog("Java", true, Sort.unsorted());
    }

    @Test
    void searchBooksWIthPriceSorting() {
        List<Book> expectedBooks = List.of(mock(Book.class));

        Sort expectedSort = Sort.by(Sort.Direction.ASC, "price");

        when(bookRepository.searchCatalog("Java", false, expectedSort)).thenReturn(expectedBooks);

        List<Book> result = catalogService.searchBooks("Java", false, true);

        assertEquals(expectedBooks, result);

        verify(bookRepository).searchCatalog("Java", false, expectedSort);
    }
}

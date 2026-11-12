package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.service.CatalogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC tests for BookController.
 * Only the web layer is started; CatalogService is replaced by a Mockito mock,
 * so these tests don't need the database.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    private Book book(String isbn, String title, String author, String price, int stock) {
        return Book.builder()
                .isbn(isbn).title(title).author(author)
                .price(new BigDecimal(price)).stockQuantity(stock)
                .build();
    }

    private final Book hobbit = book("9780261103573", "The Hobbit", "J.R.R. Tolkien", "15.99", 3);
    private final Book orwell = book("9780451524935", "1984", "George Orwell", "9.99", 0);

    // ---------- GET /books ----------

    @Test
    void listBooksShowsAllBooks() throws Exception {
        when(catalogService.getAllBooks()).thenReturn(List.of(hobbit, orwell));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attribute("books", hasSize(2)))
                .andExpect(model().attribute("query", ""))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(containsString("1984")));
    }

    @Test
    void listBooksWithEmptyCatalogShowsNoBooksMessage() throws Exception {
        when(catalogService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", empty()))
                .andExpect(content().string(containsString("No books found")));
    }

    // ---------- GET /books/search ----------

    @Test
    void searchPassesQueryAndFiltersToService() throws Exception {
        when(catalogService.searchBooks("tolkien", true, true)).thenReturn(List.of(hobbit));

        mockMvc.perform(get("/books/search")
                        .param("q", "  tolkien  ")
                        .param("inStock", "true")
                        .param("sortByPrice", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attribute("books", contains(hobbit)))
                .andExpect(model().attribute("query", "tolkien"))
                .andExpect(content().string(containsString("Results for")));

        verify(catalogService).searchBooks("tolkien", true, true);
    }

    @Test
    void searchDefaultsFiltersToFalse() throws Exception {
        when(catalogService.searchBooks("orwell", false, false)).thenReturn(List.of(orwell));

        mockMvc.perform(get("/books/search").param("q", "orwell"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", contains(orwell)));

        verify(catalogService).searchBooks("orwell", false, false);
    }

    @Test
    void searchWithNoResultsShowsEmptyList() throws Exception {
        when(catalogService.searchBooks(anyString(), anyBoolean(), anyBoolean())).thenReturn(List.of());

        mockMvc.perform(get("/books/search").param("q", "zzz"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", empty()))
                .andExpect(content().string(containsString("No books found")));
    }

    @Test
    void blankSearchRedirectsToBookList() throws Exception {
        mockMvc.perform(get("/books/search").param("q", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verifyNoInteractions(catalogService);
    }

    @Test
    void missingSearchParamRedirectsToBookList() throws Exception {
        mockMvc.perform(get("/books/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    // ---------- GET /books/{id} ----------

    @Test
    void bookDetailsShowsBook() throws Exception {
        when(catalogService.getBookById(1L)).thenReturn(Optional.of(hobbit));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/detail"))
                .andExpect(model().attribute("book", hobbit))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(containsString("9780261103573")));
    }

    @Test
    void unknownBookIdReturns404Page() throws Exception {
        when(catalogService.getBookById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/book-not-found"))
                .andExpect(content().string(containsString("Book not found")));
    }

    @Test
    void nonNumericBookIdReturns404Page() throws Exception {
        mockMvc.perform(get("/books/abc"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/book-not-found"));

        verifyNoInteractions(catalogService);
    }
}

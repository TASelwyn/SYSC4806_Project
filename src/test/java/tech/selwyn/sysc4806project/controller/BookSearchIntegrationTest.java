package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.repository.BookRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the full search-and-display flow:
 * HTTP request -> BookController -> CatalogService -> BookRepository -> H2 database -> Thymeleaf page.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private Book hobbit;

    private static Book book(String isbn, String title, String author, String price, int stock) {
        return Book.builder()
                .isbn(isbn).title(title).author(author).publisher("Test Publisher")
                .description("A test book").price(new BigDecimal(price)).stockQuantity(stock)
                .build();
    }

    @BeforeEach
    void setUp() {
        // Start every test with the same three books in the database
        bookRepository.deleteAll();
        hobbit = bookRepository.save(book("9780261103573", "The Hobbit", "J.R.R. Tolkien", "15.99", 3));
        bookRepository.save(book("9780261102385", "The Lord of the Rings", "J.R.R. Tolkien", "29.99", 0));
        bookRepository.save(book("9780451524935", "1984", "George Orwell", "9.99", 5));
    }

    @Test
    void bookListShowsEveryBookFromDatabase() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", hasSize(3)))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(containsString("The Lord of the Rings")))
                .andExpect(content().string(containsString("1984")));
    }

    @Test
    void searchByAuthorIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/books/search").param("q", "TOLKIEN"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", hasSize(2)))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(not(containsString("1984"))));
    }

    @Test
    void searchByIsbnFindsBook() throws Exception {
        mockMvc.perform(get("/books/search").param("q", "9780451524935"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(content().string(containsString("1984")));
    }

    @Test
    void searchInStockOnlyHidesOutOfStockBooks() throws Exception {
        mockMvc.perform(get("/books/search").param("q", "tolkien").param("inStock", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", hasSize(1)))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(not(containsString("The Lord of the Rings"))));
    }

    @Test
    void searchSortByPriceOrdersCheapestFirst() throws Exception {
        String html = mockMvc.perform(get("/books/search").param("q", "o").param("sortByPrice", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        int cheap = html.indexOf("1984");
        int middle = html.indexOf("The Hobbit");
        int expensive = html.indexOf("The Lord of the Rings");
        org.junit.jupiter.api.Assertions.assertTrue(cheap < middle && middle < expensive,
                "Books should be ordered by price: 1984, The Hobbit, The Lord of the Rings");
    }

    @Test
    void searchWithNoMatchesShowsMessage() throws Exception {
        mockMvc.perform(get("/books/search").param("q", "no such book"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("books", empty()))
                .andExpect(content().string(containsString("No books found")));
    }

    @Test
    void clickingASearchResultOpensItsDetailPage() throws Exception {
        // The search results page links to /books/{id}; open that link
        mockMvc.perform(get("/books/search").param("q", "hobbit"))
                .andExpect(content().string(containsString("/books/" + hobbit.getId())));

        mockMvc.perform(get("/books/" + hobbit.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/detail"))
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(containsString("9780261103573")))
                .andExpect(content().string(containsString("In stock")));
    }

    @Test
    void missingBookShowsNotFoundPage() throws Exception {
        mockMvc.perform(get("/books/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Book not found")));
    }

    @Test
    void homepageShowsFeaturedBooksFromDatabase() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome to Amazin")))
                .andExpect(content().string(containsString("The Hobbit")));
    }
}

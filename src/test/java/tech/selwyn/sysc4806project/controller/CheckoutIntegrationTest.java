package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.repository.CartRepository;
import tech.selwyn.sysc4806project.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the full purchase flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CheckoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;

    private final UUID alice = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID bob = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private Book hobbit;
    private Book orwell;

    private static Book book(String isbn, String title, String price, int stock) {
        return Book.builder()
                .isbn(isbn).title(title).author("Test Author").publisher("Test Publisher")
                .description("A test book").price(new BigDecimal(price)).stockQuantity(stock)
                .build();
    }

    @BeforeEach
    void setUp() {
        cleanDatabase();
        hobbit = bookRepo.save(book("9780261103573", "The Hobbit", "15.99", 3));
        orwell = bookRepo.save(book("9780451524935", "1984", "9.99", 5));
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        bookRepo.deleteAll();
    }

    private void putInCart(UUID userId, Book book, int quantity) throws Exception {
        mockMvc.perform(patch("/api/cart/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bookId\": \"" + book.getId() + "\", \"quantity\": " + quantity + " }"))
                .andExpect(status().isOk());
    }

    private int stockOf(Book book) {
        return bookRepo.findById(book.getId()).orElseThrow().getStockQuantity();
    }

    @Test
    void checkoutReducesStockEmptiesCartAndStoresOrderUnderUser() throws Exception {
        putInCart(alice, hobbit, 2);
        putInCart(alice, orwell, 1);

        mockMvc.perform(get("/api/checkout/" + alice + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/orders/" + alice + "/")))
                .andExpect(jsonPath("$.userId").value(alice.toString()))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.total").value(41.97));

        assertEquals(1, stockOf(hobbit));
        assertEquals(4, stockOf(orwell));

        mockMvc.perform(get("/api/cart/" + alice))
                .andExpect(jsonPath("$.items", empty()));

        assertEquals(1, orderRepo.findByUserIdOrderByCreatedAtDesc(alice).size());
        mockMvc.perform(get("/api/orders/" + alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[*].title", containsInAnyOrder("The Hobbit", "1984")));
    }

    @Test
    void checkoutWithTooLittleStockChangesNothing() throws Exception {
        putInCart(alice, hobbit, 3);
        putInCart(alice, orwell, 2);

        hobbit.setStockQuantity(1);
        bookRepo.save(hobbit);

        mockMvc.perform(get("/api/checkout/" + alice + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$[0].requested").value(3))
                .andExpect(jsonPath("$[0].available").value(1));

        mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isConflict());

        assertEquals(1, stockOf(hobbit));
        assertEquals(5, stockOf(orwell));
        assertTrue(orderRepo.findByUserIdOrderByCreatedAtDesc(alice).isEmpty());
        mockMvc.perform(get("/api/cart/" + alice))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void checkoutWithEmptyCartReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isBadRequest());

        assertTrue(orderRepo.findAll().isEmpty());
    }

    @Test
    void ordersKeepThePricePaidAfterPriceChanges() throws Exception {
        putInCart(alice, hobbit, 1);
        mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isCreated());

        hobbit.setPrice(new BigDecimal("99.99"));
        bookRepo.save(hobbit);

        mockMvc.perform(get("/api/orders/" + alice))
                .andExpect(jsonPath("$[0].items[0].unitPrice").value(15.99))
                .andExpect(jsonPath("$[0].total").value(15.99));
    }

    @Test
    void usersOnlySeeTheirOwnOrders() throws Exception {
        putInCart(alice, hobbit, 1);
        String location = mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        UUID aliceOrderId = UUID.fromString(location.substring(location.lastIndexOf('/') + 1));

        putInCart(bob, orwell, 2);
        mockMvc.perform(post("/api/checkout/" + bob))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders/" + alice))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].title").value("The Hobbit"));

        mockMvc.perform(get("/api/orders/" + bob))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].title").value("1984"));

        mockMvc.perform(get("/api/orders/" + bob + "/" + aliceOrderId))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/orders/" + alice + "/" + aliceOrderId))
                .andExpect(status().isOk());
    }

    @Test
    void buyingTheLastCopiesLeavesBookOutOfStock() throws Exception {
        putInCart(alice, hobbit, 3);
        mockMvc.perform(post("/api/checkout/" + alice))
                .andExpect(status().isCreated());
        assertEquals(0, stockOf(hobbit));

        mockMvc.perform(patch("/api/cart/" + bob)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bookId\": \"" + hobbit.getId() + "\", \"quantity\": 1 }"))
                .andExpect(status().isConflict());
    }
}

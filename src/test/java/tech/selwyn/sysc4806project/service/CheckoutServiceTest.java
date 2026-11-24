package tech.selwyn.sysc4806project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tech.selwyn.sysc4806project.dto.OrderDto;
import tech.selwyn.sysc4806project.dto.StockIssueDto;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.model.Cart;
import tech.selwyn.sysc4806project.model.Order;
import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.repository.CartRepository;
import tech.selwyn.sysc4806project.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CheckoutService.
 * Repositories are Mockito mocks, so no database is needed.
 */
@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CartRepository cartRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private CheckoutService checkoutService;

    private final UUID userId = UUID.fromString("9e7a3dd3-01cf-4fbb-a9f6-95fde15d80cb");

    private final Book hobbit = book("ec1efac6-f64c-42d9-916e-a1bff9471cc2", "The Hobbit", "15.99", 3);
    private final Book orwell = book("5b0f3c4e-2a51-4c3e-9f5d-8d1f2c7a6b90", "1984", "9.99", 5);

    private static Book book(String id, String title, String price, int stock) {
        return Book.builder()
                .id(UUID.fromString(id))
                .isbn("9780000000000")
                .title(title)
                .author("Test Author")
                .price(new BigDecimal(price))
                .stockQuantity(stock)
                .build();
    }

    private Cart cartWith(Object... bookAndQuantity) {
        Cart cart = Cart.builder().userId(userId).build();
        for (int i = 0; i < bookAndQuantity.length; i += 2) {
            cart.setBookQuantity((Book) bookAndQuantity[i], (Integer) bookAndQuantity[i + 1]);
        }
        return cart;
    }


    @Test
    void validateWithNoCartReturnsNoIssues() {
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());

        assertTrue(checkoutService.validate(userId).isEmpty());
    }

    @Test
    void validateWithEnoughStockReturnsNoIssues() {
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cartWith(hobbit, 3, orwell, 1)));

        assertTrue(checkoutService.validate(userId).isEmpty());
    }

    @Test
    void validateReportsBooksWithTooLittleStock() {
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cartWith(hobbit, 4, orwell, 1)));

        List<StockIssueDto> issues = checkoutService.validate(userId);

        assertEquals(1, issues.size());
        assertEquals(hobbit.getId(), issues.getFirst().bookId());
        assertEquals(4, issues.getFirst().requested());
        assertEquals(3, issues.getFirst().available());
        assertEquals("Only 3 left of The Hobbit (you asked for 4)", issues.getFirst().message());
    }



    @Test
    void checkoutWithNoCartThrowsBadRequest() {
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> checkoutService.checkout(userId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(bookRepo, orderRepo);
    }

    @Test
    void checkoutWithEmptyCartThrowsBadRequest() {
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cartWith()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> checkoutService.checkout(userId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(bookRepo, orderRepo);
    }

    @Test
    void checkoutWithTooLittleStockThrowsConflictAndChangesNothing() {
        Cart cart = cartWith(hobbit, 10);
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cart));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> checkoutService.checkout(userId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Only 3 left of The Hobbit (you asked for 10)", ex.getReason());
        verify(bookRepo, never()).decrementStock(any(), anyInt());
        verify(orderRepo, never()).save(any());
        assertEquals(1, cart.getItems().size(), "cart must be kept");
    }

    @Test
    void checkoutThrowsConflictWhenStockRunsOutDuringCheckout() {
        // Stock looked fine, but another buyer took the copies before our update ran
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cartWith(hobbit, 2)));
        when(bookRepo.decrementStock(hobbit.getId(), 2)).thenReturn(0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> checkoutService.checkout(userId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(orderRepo, never()).save(any());
    }

    @Test
    void checkoutSavesOrderUnderUserAndEmptiesCart() {
        Cart cart = cartWith(hobbit, 2, orwell, 1);
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepo.decrementStock(any(), anyInt())).thenReturn(1);
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instant before = Instant.now();
        OrderDto result = checkoutService.checkout(userId);


        verify(bookRepo).decrementStock(hobbit.getId(), 2);
        verify(bookRepo).decrementStock(orwell.getId(), 1);


        ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
        verify(orderRepo).save(saved.capture());
        Order order = saved.getValue();
        assertEquals(userId, order.getUserId());
        assertFalse(order.getCreatedAt().isBefore(before));
        assertEquals(new BigDecimal("41.97"), order.getTotal());
        assertEquals(2, order.getItems().size());
        assertEquals("The Hobbit", order.getItems().getFirst().getTitle());
        assertEquals(new BigDecimal("15.99"), order.getItems().getFirst().getUnitPrice());
        assertEquals(2, order.getItems().getFirst().getQuantity());
        assertSame(order, order.getItems().getFirst().getOrder());

        assertEquals(userId, result.userId());
        assertEquals(new BigDecimal("41.97"), result.total());
        assertEquals(new BigDecimal("31.98"), result.items().getFirst().subtotal());


        assertTrue(cart.getItems().isEmpty());
    }



    @Test
    void getOrdersReturnsUsersOrders() {
        Order order = Order.builder()
                .id(UUID.randomUUID()).userId(userId).createdAt(Instant.now())
                .total(new BigDecimal("15.99")).build();
        when(orderRepo.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(order));

        List<OrderDto> orders = checkoutService.getOrders(userId);

        assertEquals(1, orders.size());
        assertEquals(order.getId(), orders.getFirst().id());
    }

    @Test
    void getOrderOfAnotherUserThrowsNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepo.findByIdAndUserId(orderId, userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> checkoutService.getOrder(userId, orderId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}

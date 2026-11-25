package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tech.selwyn.sysc4806project.dto.OrderDto;
import tech.selwyn.sysc4806project.dto.OrderItemDto;
import tech.selwyn.sysc4806project.dto.StockIssueDto;
import tech.selwyn.sysc4806project.service.CheckoutService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC tests for CheckoutController.
 * Only the web layer is started; CheckoutService is a Mockito mock.
 */
@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutService checkoutService;

    private final UUID userId = UUID.fromString("9e7a3dd3-01cf-4fbb-a9f6-95fde15d80cb");
    private final UUID orderId = UUID.fromString("0b5c7c1e-8f3a-4d2b-9a61-3e0f5d7c2a11");
    private final UUID hobbitBookId = UUID.fromString("ec1efac6-f64c-42d9-916e-a1bff9471cc2");

    private final OrderDto hobbitOrder = new OrderDto(
            orderId, userId, Instant.parse("2026-11-23T15:00:00Z"),
            List.of(new OrderItemDto(hobbitBookId, "The Hobbit", new BigDecimal("15.99"), 2, new BigDecimal("31.98"))),
            new BigDecimal("31.98"));

    // GET /api/checkout/{userId}/validate

    @Test
    void validateReturnsEmptyListWhenCartIsReady() throws Exception {
        when(checkoutService.validate(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/checkout/" + userId + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }


    // GET /api/checkout/{userId}/validate
    @Test
    void validateReturnsStockIssues() throws Exception {
        when(checkoutService.validate(userId))
                .thenReturn(List.of(new StockIssueDto(hobbitBookId, "The Hobbit", 5, 3)));

        mockMvc.perform(get("/api/checkout/" + userId + "/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$[0].requested").value(5))
                .andExpect(jsonPath("$[0].available").value(3));
    }

    // POST /api/checkout/{userId}

    @Test
    void checkoutReturnsCreatedOrder() throws Exception {
        when(checkoutService.checkout(userId)).thenReturn(hobbitOrder);

        mockMvc.perform(post("/api/checkout/" + userId))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/" + userId + "/" + orderId))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(15.99))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.total").value(31.98));

        verify(checkoutService).checkout(userId);
    }

    @Test
    void checkoutWithEmptyCartReturnsBadRequest() throws Exception {
        when(checkoutService.checkout(userId))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

        mockMvc.perform(post("/api/checkout/" + userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutWithTooLittleStockReturnsConflict() throws Exception {
        when(checkoutService.checkout(userId))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Only 3 left of The Hobbit (you asked for 5)"));

        mockMvc.perform(post("/api/checkout/" + userId))
                .andExpect(status().isConflict());
    }

    @Test
    void checkoutWithInvalidUuidReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/checkout/not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(checkoutService);
    }

    // GET /api/orders/{userId} and /api/orders/{userId}/{orderId}

    @Test
    void getOrdersReturnsUsersOrders() throws Exception {
        when(checkoutService.getOrders(userId)).thenReturn(List.of(hobbitOrder));

        mockMvc.perform(get("/api/orders/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(orderId.toString()));
    }

    @Test
    void getOrderReturnsOneOrder() throws Exception {
        when(checkoutService.getOrder(userId, orderId)).thenReturn(hobbitOrder);

        mockMvc.perform(get("/api/orders/" + userId + "/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.total").value(31.98));
    }

    @Test
    void getUnknownOrderReturnsNotFound() throws Exception {
        when(checkoutService.getOrder(userId, orderId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        mockMvc.perform(get("/api/orders/" + userId + "/" + orderId))
                .andExpect(status().isNotFound());
    }
}

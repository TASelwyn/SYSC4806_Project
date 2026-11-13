package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tech.selwyn.sysc4806project.dto.CartDto;
import tech.selwyn.sysc4806project.dto.CartItemDto;
import tech.selwyn.sysc4806project.dto.UpdateCartRequestDto;
import tech.selwyn.sysc4806project.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC tests for CartController.
 * Only the web layer is started; CartService is replaced by a Mockito mock,
 * so these tests don't need the database.
 */
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    // can be anything tbf
    private final UUID userId = UUID.fromString("9e7a3dd3-01cf-4fbb-a9f6-95fde15d80cb");
    private final UUID hobbitBookId = UUID.fromString("ec1efac6-f64c-42d9-916e-a1bff9471cc2");

    private final String url = "/api/cart/" + userId;

    private final CartItemDto hobbitLine = new CartItemDto(
            hobbitBookId, "9780261103573", "The Hobbit", "J.R.R. Tolkien",
            new BigDecimal("15.99"), 2, new BigDecimal("31.98"));

    private final CartDto emptyCart = new CartDto(List.of(), BigDecimal.ZERO);
    private final CartDto cartWithHobbit = new CartDto(List.of(hobbitLine), new BigDecimal("31.98"));

    // GET /api/cart/{uuid}

    @Test
    void getCartReturnsUsersCart() throws Exception {
        when(cartService.getOrCreateCart(userId)).thenReturn(cartWithHobbit);

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].bookId").value("ec1efac6-f64c-42d9-916e-a1bff9471cc2"))
                .andExpect(jsonPath("$.items[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].subtotal").value(31.98))
                .andExpect(jsonPath("$.total").value(31.98));
    }

    @Test
    void getCartForNewUserReturnsEmptyCart() throws Exception {
        when(cartService.getOrCreateCart(userId)).thenReturn(emptyCart);

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", empty()))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void getCartWithInvalidUuidReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/cart/not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    // PATCH /api/cart/{uuid}

    @Test
    void patchSetsBookQuantity() throws Exception {
        when(cartService.updateItem(userId, new UpdateCartRequestDto(hobbitBookId, 2))).thenReturn(cartWithHobbit);

        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bookId": "ec1efac6-f64c-42d9-916e-a1bff9471cc2", "quantity": 2 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.total").value(31.98));

        verify(cartService).updateItem(userId, new UpdateCartRequestDto(hobbitBookId, 2));
    }

    @Test
    void patchWithZeroQuantityRemovesBook() throws Exception {
        when(cartService.updateItem(userId, new UpdateCartRequestDto(hobbitBookId, 0))).thenReturn(emptyCart);

        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bookId": "ec1efac6-f64c-42d9-916e-a1bff9471cc2", "quantity": 0 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", empty()));
    }

    @Test
    void patchWithNegativeQuantityReturnsBadRequest() throws Exception {
        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bookId": "ec1efac6-f64c-42d9-916e-a1bff9471cc2", "quantity": -1 }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }


    @Test
    void patchUnknownBookReturnsNotFound() throws Exception {
        when(cartService.updateItem(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        // This book id is purposefully not anywhere else.
        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bookId": "e2d62df8-df10-438b-9e66-e95adce50d29", "quantity": 1 }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchMoreThanStockReturnsConflict() throws Exception {
        when(cartService.updateItem(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Only 3 in stock"));

        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bookId": "ec1efac6-f64c-42d9-916e-a1bff9471cc2", "quantity": 50 }
                                """))
                .andExpect(status().isConflict());
    }

    // DELETE /api/cart/{uuid}

    @Test
    void deleteExistingCartReturnsOkayNoContent() throws Exception {
        when(cartService.deleteByUserId(userId)).thenReturn(true);

        mockMvc.perform(delete(url))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(cartService).deleteByUserId(userId);
    }
}

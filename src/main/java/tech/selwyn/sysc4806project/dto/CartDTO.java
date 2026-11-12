package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Read-only view of a cart returned to clients
 */
public record CartDTO(UUID id, UUID userUuid, List<CartItemDTO> items, BigDecimal total) {

    public static CartDTO from(Cart cart) {
        return new CartDTO(
                cart.getId(),
                cart.getUserUuid(),
                cart.getItems().stream().map(CartItemDTO::from).toList(),
                cart.getTotal());
    }
}

package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read-only view of a cart returned to clients
 */
public record CartDto(List<CartItemDto> items, BigDecimal total) {

    public static CartDto from(Cart cart) {
        return new CartDto(
                cart.getItems().stream().map(CartItemDto::from).toList(),
                cart.getTotal());
    }
}

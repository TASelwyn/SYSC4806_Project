package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(UUID bookId, String title, BigDecimal unitPrice,
                           int quantity, BigDecimal subtotal) {
    public static OrderItemDto from(OrderItem item) {
        return new OrderItemDto(
                item.getBook().getId(),
                item.getTitle(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }
}

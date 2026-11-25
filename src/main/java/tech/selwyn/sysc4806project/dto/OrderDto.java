package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A order returned to clients
 */

public record OrderDto(UUID id, UUID userId, Instant createdAt,
                       List<OrderItemDto> items, BigDecimal total) {

    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemDto::from).toList(),
                order.getTotal());
    }
}

package tech.selwyn.sysc4806project.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.selwyn.sysc4806project.dto.OrderDto;
import tech.selwyn.sysc4806project.dto.StockIssueDto;
import tech.selwyn.sysc4806project.service.CheckoutService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for the simulated checkout and a user's order history.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CheckoutController {
    private final CheckoutService checkoutService;


    @GetMapping("/checkout/{userId}/validate")
    public ResponseEntity<List<StockIssueDto>> validate(@PathVariable UUID userId) {
        return ResponseEntity.ok(checkoutService.validate(userId));
    }


    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderDto> checkout(@PathVariable UUID userId) {
        OrderDto order = checkoutService.checkout(userId);
        return ResponseEntity
                .created(URI.create("/api/orders/" + userId + "/" + order.id()))
                .body(order);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable UUID userId) {
        return ResponseEntity.ok(checkoutService.getOrders(userId));
    }

    @GetMapping("/orders/{userId}/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID userId, @PathVariable UUID orderId) {
        return ResponseEntity.ok(checkoutService.getOrder(userId, orderId));
    }
}

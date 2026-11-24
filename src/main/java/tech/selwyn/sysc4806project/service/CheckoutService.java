package tech.selwyn.sysc4806project.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tech.selwyn.sysc4806project.dto.OrderDto;
import tech.selwyn.sysc4806project.dto.StockIssueDto;
import tech.selwyn.sysc4806project.model.Cart;
import tech.selwyn.sysc4806project.model.CartItem;
import tech.selwyn.sysc4806project.model.Order;
import tech.selwyn.sysc4806project.model.OrderItem;
import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.repository.CartRepository;
import tech.selwyn.sysc4806project.repository.OrderRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Simulated checkout: turns a user's cart into a stored order and takes the books out of stock.
 */

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepo;
    private final BookRepository bookRepo;
    private final OrderRepository orderRepo;

    @Transactional(readOnly = true)
    public List<StockIssueDto> validate(UUID userId) {
        return cartRepo.findByUserId(userId)
                .map(this::findStockIssues)
                .orElse(List.of());
    }

    /**
     * Places an order if all items in stock.
     */

    @Transactional
    public OrderDto checkout(UUID userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

        List<StockIssueDto> issues = findStockIssues(cart);
        if (!issues.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, describe(issues));
        }

        Order order = Order.builder()
                .userId(userId)
                .createdAt(Instant.now())
                .total(cart.getTotal())
                .build();

        for (CartItem item : cart.getItems()) {
            int updated = bookRepo.decrementStock(item.getBook().getId(), item.getQuantity());
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Not enough stock for " + item.getBook().getTitle());
            }
            order.getItems().add(OrderItem.fromCartItem(order, item));
        }

        Order saved = orderRepo.save(order);

        cart.getItems().clear();

        return OrderDto.from(saved);
    }

    /**
     * All of a user's orders
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(UUID userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderDto::from)
                .toList();
    }

    /**
     * One order, only if it belongs to the user
     *
     */

    @Transactional(readOnly = true)
    public OrderDto getOrder(UUID userId, UUID orderId) {
        return orderRepo.findByIdAndUserId(orderId, userId)
                .map(OrderDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private List<StockIssueDto> findStockIssues(Cart cart) {
        return cart.getItems().stream()
                .filter(item -> item.getQuantity() > item.getBook().getStockQuantity())
                .map(StockIssueDto::from)
                .toList();
    }

    private String describe(List<StockIssueDto> issues) {
        return issues.stream()
                .map(StockIssueDto::message)
                .collect(Collectors.joining("; "));
    }
}

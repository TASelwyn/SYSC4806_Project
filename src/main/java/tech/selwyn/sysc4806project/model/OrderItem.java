package tech.selwyn.sysc4806project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Unlike CartItem, the price is copied so later price changes don't alter past orders.
 */
@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    
    @Column(nullable = false)
    private String title;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Positive
    @Column(nullable = false)
    private int quantity;

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

   
    public static OrderItem fromCartItem(Order order, CartItem cartItem) {
        Book book = cartItem.getBook();
        return OrderItem.builder()
                .order(order)
                .book(book)
                .title(book.getTitle())
                .unitPrice(book.getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}
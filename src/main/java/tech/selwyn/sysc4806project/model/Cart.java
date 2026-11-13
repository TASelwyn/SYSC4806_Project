package tech.selwyn.sysc4806project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Cart {
    /**
     * Cart owner's user id
     */
    @Id
    @Column(nullable = false, unique = true)
    @JsonIgnore
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /**
     * Sets how many copies of a book are in the cart. 0 removes the book.
     */
    public void setBookQuantity(Book book, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity must not be negative");

        if (quantity == 0) {
            removeBook(book.getId());
            return;
        }

        findItem(book.getId()).ifPresentOrElse(
                item -> item.setQuantity(quantity),
                () -> items.add(CartItem.builder().cart(this).book(book).quantity(quantity).build()));
    }

    /**
     * Removes a book from the cart entirely.
     */
    public void removeBook(UUID bookId) {
        items.removeIf(item -> item.getBook().getId().equals(bookId));
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<CartItem> findItem(UUID bookId) {
        return items.stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();
    }
}

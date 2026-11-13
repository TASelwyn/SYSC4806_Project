package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.model.CartItem;

import java.math.BigDecimal;

/**
 * Read-only view of one cart line returned to clients
 */
public record CartItemDto(Long bookId, String isbn, String title, String author,
                          BigDecimal unitPrice, int quantity, BigDecimal subtotal) {

    public static CartItemDto from(CartItem item) {
        Book book = item.getBook();
        return new CartItemDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }
}

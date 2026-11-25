package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.CartItem;

import java.util.UUID;

/**
 Returned by stock validation when asked for more copies than stock, so the checkout page can warn the user.
 */
public record StockIssueDto(UUID bookId, String title, int requested, int available) {

    public static StockIssueDto from(CartItem item) {
        return new StockIssueDto(
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getQuantity(),
                item.getBook().getStockQuantity());
    }

    public String message() {
        return available == 0
                ? title + " is out of stock"
                : "Only " + available + " left of " + title + " (you asked for " + requested + ")";
    }
}

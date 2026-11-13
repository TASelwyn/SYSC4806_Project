package tech.selwyn.sysc4806project.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tech.selwyn.sysc4806project.dto.CartDto;
import tech.selwyn.sysc4806project.dto.UpdateCartRequestDto;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.model.Cart;
import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.repository.CartRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final BookRepository bookRepo;
    private final CartRepository cartRepo;

    @Transactional(readOnly = true)
    public Optional<CartDto> findByUserId(UUID userId) {
        return cartRepo.findByUserId(userId).map(CartDto::from);
    }

    @Transactional
    public CartDto getOrCreateCart(UUID userId) {
        return CartDto.from(findOrCreate(userId));
    }

    /**
     * Sets how many copies of a book are in the user's cart (0 removes it).
     * Creates the cart if the user doesn't have one yet.
     */
    @Transactional
    public CartDto updateItem(UUID userId, UpdateCartRequestDto req) {
        Cart cart = findOrCreate(userId);

        if (req.quantity() == 0) {
            cart.removeBook(req.bookId());
        } else {
            Book book = bookRepo.findById(req.bookId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

            if (req.quantity() > book.getStockQuantity()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Only " + book.getStockQuantity() + " in stock");
            }

            cart.setBookQuantity(book, req.quantity());
        }

        return CartDto.from(cartRepo.save(cart));
    }

    private Cart findOrCreate(UUID userId) {
        return cartRepo.findByUserId(userId)
                .orElseGet(() -> cartRepo.save(Cart.builder().userId(userId).build()));
    }

    @Transactional
    public boolean deleteByUserId(UUID userId) {
        return cartRepo.deleteByUserId(userId) > 0;
    }
}

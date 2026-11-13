package tech.selwyn.sysc4806project.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.selwyn.sysc4806project.dto.CartDto;
import tech.selwyn.sysc4806project.dto.UpdateCartRequestDto;
import tech.selwyn.sysc4806project.service.CartService;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<CartDto> updateCart(@PathVariable UUID userId,
                                              @Valid @RequestBody UpdateCartRequestDto req) {
        return ResponseEntity.ok(cartService.updateItem(userId, req));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCart(@PathVariable UUID userId) {
        return cartService.deleteByUserId(userId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

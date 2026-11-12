package tech.selwyn.sysc4806project.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.selwyn.sysc4806project.dto.CartDTO;
import tech.selwyn.sysc4806project.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userUuid}")
    public ResponseEntity<CartDTO> getCart(@PathVariable UUID userUuid) {
        return cartService.findByUserUuid(userUuid)
                .map(cart -> ResponseEntity.ok()
                        .eTag(String.valueOf(cart.version()))
                        .body(CartResponse.from(cart)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{userUuid}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable UUID userUuid) {
        return cartService.findByUserUuid(userUuid)
                .map(cart -> ResponseEntity.ok()
                        .eTag(String.valueOf(cart.version()))
                        .body(CartResponse.from(cart)))
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{userUuid}")
    public ResponseEntity<Void> deleteCart(@PathVariable UUID userUuid) {
        return cartService.deleteByUserUuid(userUuid)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

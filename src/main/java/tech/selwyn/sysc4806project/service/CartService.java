package tech.selwyn.sysc4806project.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.selwyn.sysc4806project.dto.CartDTO;
import tech.selwyn.sysc4806project.model.Cart;
import tech.selwyn.sysc4806project.repository.BookRepository;
import tech.selwyn.sysc4806project.repository.CartRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final BookRepository bookRepo;
    private final CartRepository cartRepo;

    public Optional<CartDTO> findByUserUuid(UUID userUuid) {
       return cartRepo.findByUserUuid(userUuid).map(CartDTO::from);
    }

    @Transactional
    public boolean deleteByUserUuid(UUID userUuid) {
        return cartRepo.deleteByUserUuid(userUuid) > 0;
    }


}

package tech.selwyn.sysc4806project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.selwyn.sysc4806project.model.Cart;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing shopping carts
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserUuid(UUID userUuid);

    long deleteByUserUuid(UUID userUuid);

    boolean existsByUserUuid(UUID userUuid);
}

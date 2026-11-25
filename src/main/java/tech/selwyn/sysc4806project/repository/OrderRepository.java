package tech.selwyn.sysc4806project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.selwyn.sysc4806project.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for completed order
 */

public interface OrderRepository extends JpaRepository<Order, UUID> {
    /**
     * All orders a user has placed
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * One order, only if it belongs to the given user */
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);
}

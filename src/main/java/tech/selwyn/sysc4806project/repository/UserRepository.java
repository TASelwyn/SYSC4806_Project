package tech.selwyn.sysc4806project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.selwyn.sysc4806project.model.User;

import java.util.UUID;

/**
 * Repository for reading and saving users.
 * Standard CRUD operations (findAll, findById, save, ...) come from JpaRepository.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
}

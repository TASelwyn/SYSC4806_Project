package tech.selwyn.sysc4806project.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.selwyn.sysc4806project.dto.UserDto;
import tech.selwyn.sysc4806project.repository.UserRepository;

import java.util.List;

/**
 * Application logic for users.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Returns every user as a UserDto (id, role, firstName).
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::from)
                .toList();
    }
}

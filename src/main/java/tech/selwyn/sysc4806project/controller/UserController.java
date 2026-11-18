package tech.selwyn.sysc4806project.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.selwyn.sysc4806project.dto.UserDto;
import tech.selwyn.sysc4806project.service.UserService;

import java.util.List;

/**
 * REST API for users. Used by the header's user toggle dropdown.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * GET /api/users
     * Returns all users as JSON: [{"id": "...", "role": "ROLE_USER", "firstName": "Alice"}, ...]
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}

package tech.selwyn.sysc4806project.dto;

import tech.selwyn.sysc4806project.model.RoleType;
import tech.selwyn.sysc4806project.model.User;

import java.util.UUID;

/**
 * Public view of a user returned by GET /api/users.
 * Only exposes what the user toggle needs (no email or last name).
 */
public record UserDto(UUID id, RoleType role, String firstName) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getRole(), user.getFirstName());
    }
}

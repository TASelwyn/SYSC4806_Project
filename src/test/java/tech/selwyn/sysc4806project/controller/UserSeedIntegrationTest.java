package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.model.RoleType;
import tech.selwyn.sysc4806project.model.User;
import tech.selwyn.sysc4806project.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the user toggle:
 * db/seed-users.sql -> H2 users table -> UserRepository -> UserService -> GET /api/users.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserSeedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void seedCreatesThreeUsersAndOneAdmin() {
        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(4);
        assertThat(users).filteredOn(u -> u.getRole() == RoleType.ROLE_ADMIN).hasSize(1);
        assertThat(users).filteredOn(u -> u.getRole() == RoleType.ROLE_USER).hasSize(3);
    }

    @Test
    void getUsersReturnsAllSeededUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].firstName", hasItem("Olivia")))
                .andExpect(jsonPath("$[*].firstName", hasItem("Alice")))
                .andExpect(jsonPath("$[*].role", hasItem("ROLE_ADMIN")));
    }

    @Test
    void seededAdminKeepsItsFixedId() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.role == 'ROLE_ADMIN')].id")
                        .value(hasItem("ff471d55-aa94-4301-9476-947f77914498")));
    }
}

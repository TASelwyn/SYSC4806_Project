package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.dto.UserDto;
import tech.selwyn.sysc4806project.model.RoleType;
import tech.selwyn.sysc4806project.service.UserService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getUsersReturnsIdRoleAndFirstName() throws Exception {
        UUID adminId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID aliceId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(userService.getAllUsers()).thenReturn(List.of(
                new UserDto(adminId, RoleType.ROLE_ADMIN, "Olivia"),
                new UserDto(aliceId, RoleType.ROLE_USER, "Alice")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(adminId.toString()))
                .andExpect(jsonPath("$[0].role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[0].firstName").value("Olivia"))
                .andExpect(jsonPath("$[1].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].role").value("ROLE_USER"));
    }

    @Test
    void getUsersDoesNotExposeEmailOrLastName() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                new UserDto(UUID.randomUUID(), RoleType.ROLE_USER, "Bob")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].lastName").doesNotExist());
    }

    @Test
    void getUsersReturnsEmptyListWhenThereAreNoUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

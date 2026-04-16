package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.AuthResponse;
import com.vasu.conference_management.entity.Role;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTests {

    private AutoCloseable mocks;
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void loginSucceedsWithValidCredentials() throws Exception {
        // Setup: Mock user with roles
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        Role authorRole = new Role(1L, "AUTHOR", "Can submit papers");
        user.setRoles(Set.of(authorRole));

        // Mock the service to return user
        when(authService.login("testuser", "password123")).thenReturn(user);

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.roles[0]").value("AUTHOR"));
    }

    @Test
    void loginFailsWithInvalidPassword() throws Exception {
        // Setup: Mock service to throw exception
        when(authService.login("testuser", "wrongpassword"))
                .thenThrow(new IllegalStateException("Invalid password for user: testuser"));

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password for user: testuser"));
    }

    @Test
    void loginFailsWithNonexistentUser() throws Exception {
        // Setup: Mock service to throw exception
        when(authService.login("nonexistent", "password123"))
                .thenThrow(new IllegalStateException("User not found: nonexistent"));

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nonexistent\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not found: nonexistent"));
    }

    @Test
    void loginFailsWithNullUsername() throws Exception {
        // Setup: Mock service to throw exception
        when(authService.login(null, "password123"))
                .thenThrow(new IllegalArgumentException("Username cannot be null or empty"));

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":null,\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username cannot be null or empty"));
    }

    @Test
    void loginFailsWithDisabledUser() throws Exception {
        // Setup: Mock service to throw exception
        when(authService.login("disableduser", "password123"))
                .thenThrow(new IllegalStateException("User account is disabled: disableduser"));

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"disableduser\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User account is disabled: disableduser"));
    }

    @Test
    void loginWithMultipleRoles() throws Exception {
        // Setup: Mock user with multiple roles
        User user = new User();
        user.setId(2L);
        user.setUsername("multiroleuser");
        user.setEmail("multi@example.com");
        user.setFirstName("Multi");
        user.setLastName("Role");

        Role authorRole = new Role(1L, "AUTHOR", "Can submit papers");
        Role reviewerRole = new Role(2L, "REVIEWER", "Can review papers");
        user.setRoles(Set.of(authorRole, reviewerRole));

        // Mock the service
        when(authService.login("multiroleuser", "password123")).thenReturn(user);

        // Test: Send login request
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"multiroleuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.roles.length()").value(2))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void logoutSucceeds() throws Exception {
        // Test: Send logout request
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void loginResponseIncludesAllUserFields() throws Exception {
        // Setup: Mock user with all fields
        User user = new User();
        user.setId(99L);
        user.setUsername("completeuser");
        user.setEmail("complete@example.com");
        user.setFirstName("Complete");
        user.setLastName("User");

        Role adminRole = new Role(3L, "ADMIN", "Administrator");
        user.setRoles(Set.of(adminRole));

        // Mock the service
        when(authService.login("completeuser", "password123")).thenReturn(user);

        // Test: Verify all fields in response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"completeuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(99L))
                .andExpect(jsonPath("$.username").value("completeuser"))
                .andExpect(jsonPath("$.email").value("complete@example.com"))
                .andExpect(jsonPath("$.firstName").value("Complete"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}















package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.Role;
import com.vasu.conference_management.repository.UserRepository;
import com.vasu.conference_management.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final AtomicInteger testCounter = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        // Clear existing users before each test
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            // Ignore errors from clearing if DB is fresh
        }
    }

    @Test
    void loginSucceedsWithValidCredentials() {
        // Setup: Create user with encoded password
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser" + testCounter.incrementAndGet() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);

        userRepository.save(user);

        // Test: Login with correct credentials
        User loggedInUser = authService.login("testuser", "password123");

        // Verify
        assertNotNull(loggedInUser);
        assertEquals("testuser", loggedInUser.getUsername());
    }

    @Test
    void loginFailsWithInvalidPassword() {
        // Setup: Create user
        User user = new User();
        user.setUsername("testuser2");
        user.setEmail("testuser2" + testCounter.incrementAndGet() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);

        userRepository.save(user);

        // Test & Verify: Login with wrong password throws exception
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.login("testuser2", "wrongpassword")
        );

        assertEquals("Invalid password for user: testuser2", exception.getMessage());
    }

    @Test
    void loginFailsWithNonexistentUser() {
        // Test & Verify: Login with nonexistent user throws exception
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.login("nonexistent", "password123")
        );

        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void loginIsCaseInsensitive() {
        // Setup: Create user with unique username
        int counter = testCounter.incrementAndGet();
        User user = new User();
        user.setUsername("TestUser" + counter);
        user.setEmail("testuser" + counter + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);

        userRepository.save(user);

        // Test: Login with different case
        User loggedInUser = authService.login("testuser" + counter, "password123");

        // Verify
        assertNotNull(loggedInUser);
        assertEquals("TestUser" + counter, loggedInUser.getUsername());
    }

    @Test
    void loginFailsWhenUserDisabled() {
        // Setup: Create disabled user
        User user = new User();
        user.setUsername("disableduser");
        user.setEmail("disableduser" + testCounter.incrementAndGet() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(false);

        userRepository.save(user);

        // Test & Verify: Login throws exception
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authService.login("disableduser", "password123")
        );

        assertEquals("User account is disabled: disableduser", exception.getMessage());
    }

    @Test
    void loginFailsWithNullUsername() {
        // Test & Verify: Null username throws exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(null, "password123")
        );

        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    @Test
    void loginFailsWithEmptyUsername() {
        // Test & Verify: Empty username throws exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("   ", "password123")
        );

        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    @Test
    void loginFailsWithNullPassword() {
        // Setup: Create user
        User user = new User();
        user.setUsername("testuser4");
        user.setEmail("testuser4" + testCounter.incrementAndGet() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);

        userRepository.save(user);

        // Test & Verify: Null password throws exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("testuser4", null)
        );

        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void encodePasswordSucceeds() {
        // Setup
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainpassword");

        // Test: Encode password
        User encodedUser = authService.encodePassword(user);

        // Verify: Password is encoded (not same as original)
        assertNotNull(encodedUser.getPassword());
        assertNotEquals("plainpassword", encodedUser.getPassword());
        assertTrue(passwordEncoder.matches("plainpassword", encodedUser.getPassword()));
    }

    @Test
    void encodePasswordFailsWithNullUser() {
        // Test & Verify
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.encodePassword(null)
        );

        assertEquals("User and password cannot be null", exception.getMessage());
    }

    @Test
    void encodePasswordFailsWithNullPassword() {
        // Setup
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(null);

        // Test & Verify
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.encodePassword(user)
        );

        assertEquals("User and password cannot be null", exception.getMessage());
    }

    @Test
    void verifyPasswordSucceeds() {
        // Setup
        String plainPassword = "mypassword";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Test & Verify
        assertTrue(authService.verifyPassword(plainPassword, encodedPassword));
    }

    @Test
    void verifyPasswordFailsWithWrongPassword() {
        // Setup
        String encodedPassword = passwordEncoder.encode("mypassword");

        // Test & Verify
        assertFalse(authService.verifyPassword("wrongpassword", encodedPassword));
    }

    @Test
    void verifyPasswordFailsWithNullInputs() {
        // Setup
        String encodedPassword = passwordEncoder.encode("mypassword");

        // Test & Verify
        assertFalse(authService.verifyPassword(null, encodedPassword));
        assertFalse(authService.verifyPassword("password", null));
        assertFalse(authService.verifyPassword(null, null));
    }

    @Test
    void loginPreservesUserRoles() {
        // Setup: Create or reuse existing roles
        Role authorRole = roleRepository.findByRoleName("AUTHOR")
                .orElseGet(() -> roleRepository.save(new Role(null, "AUTHOR", "Can submit papers")));
        Role reviewerRole = roleRepository.findByRoleName("REVIEWER")
                .orElseGet(() -> roleRepository.save(new Role(null, "REVIEWER", "Can review papers")));

        User user = new User();
        user.setUsername("multiroleuser");
        user.setEmail("multiroleuser" + testCounter.incrementAndGet() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);
        user.setRoles(Set.of(authorRole, reviewerRole));

        userRepository.save(user);

        // Test: Login
        User loggedInUser = authService.login("multiroleuser", "password123");

        // Verify: Roles are preserved
        assertNotNull(loggedInUser.getRoles());
        assertEquals(2, loggedInUser.getRoles().size());
        assertTrue(loggedInUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("AUTHOR")));
        assertTrue(loggedInUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("REVIEWER")));
    }
}












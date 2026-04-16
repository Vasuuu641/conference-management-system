package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user with username and password.
     * Throws exception if user not found or password mismatch.
     *
     * @param username case-insensitive username
     * @param password plaintext password to verify against stored hash
     * @return User with roles loaded
     * @throws IllegalStateException if user not found or password invalid
     */
    @Transactional(readOnly = true)
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        if (!user.getEnabled()) {
            throw new IllegalStateException("User account is disabled: " + username);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Invalid password for user: " + username);
        }

        return user;
    }

    /**
     * Register a new user with encoded password.
     * Note: This is different from UserService.register() - this is for raw password encoding
     * during authentication setup. UserService handles full registration with validation.
     *
     * @param user User entity with plaintext password
     * @return User with encoded password (not saved to database)
     */
    public User encodePassword(User user) {
        if (user == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User and password cannot be null");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    /**
     * Verify if a plaintext password matches the stored encoded password.
     *
     * @param plainPassword plaintext password to check
     * @param encodedPassword BCrypt-encoded password from database
     * @return true if passwords match, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String encodedPassword) {
        if (plainPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
}


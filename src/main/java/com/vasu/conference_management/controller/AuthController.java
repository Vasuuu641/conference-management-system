package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.AuthResponse;
import com.vasu.conference_management.dto.LoginRequest;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint for REST clients.
     * Validates username/password and establishes session-based authentication.
     *
     * @param request LoginRequest with username and password
     * @param session HttpSession to establish authenticated session
     * @return AuthResponse with user details and roles on success
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            // Authenticate user
            User user = authService.login(request.getUsername(), request.getPassword());

            // Create Spring Security Authentication
            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                    .collect(Collectors.toList());

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    authorities
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Build response
            AuthResponse response = new AuthResponse();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setRoles(user.getRoles().stream()
                    .map(role -> role.getRoleName())
                    .collect(Collectors.toSet()));
            response.setMessage("Login successful");

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, null, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, null, null, null, null, null, e.getMessage()));
        }
    }

    /**
     * Logout endpoint for REST clients.
     * Clears authentication from security context and invalidates session.
     *
     * @param session HttpSession to invalidate
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();

        AuthResponse response = new AuthResponse();
        response.setMessage("Logout successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user info.
     * Returns the details of the currently logged-in user.
     *
     * @return AuthResponse with current user details or 401 if not authenticated
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is not authenticated or is anonymous
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, null, "Not authenticated"));
        }

        Object principal = auth.getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, null, null, null, "Not authenticated"));
        }

        // In a real scenario, you'd fetch user details from database
        // For now, return basic info from authentication
        AuthResponse response = new AuthResponse();
        response.setUsername(auth.getName());
        response.setMessage("User is authenticated");

        return ResponseEntity.ok(response);
    }
}



package com.vasu.conference_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
    private String message;
}


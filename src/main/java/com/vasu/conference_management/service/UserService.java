package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.Role;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.UserProfile;
import com.vasu.conference_management.repository.RoleRepository;
import com.vasu.conference_management.repository.UserProfileRepository;
import com.vasu.conference_management.repository.UserRepository;
import com.vasu.conference_management.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public User register(User user, Set<String> roleNames) {
        if (user == null) {
            throw new IllegalArgumentException("user must be provided");
        }
        ValidationUtil.requireNonEmpty(roleNames, "roleNames");

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }

        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalStateException("Email already exists: " + user.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            String normalized = roleName == null ? null : roleName.trim().toUpperCase();
            if (normalized == null || normalized.isBlank()) {
                continue;
            }
            Role role = roleRepository.findByRoleName(normalized)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + normalized));
            roles.add(role);
        }
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("At least one valid role is required");
        }

        user.setEnabled(user.getEnabled() == null || user.getEnabled());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        ValidationUtil.requirePositiveId(id, "id");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        return userRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    @Transactional
    public UserProfile upsertProfile(Long userId, UserProfile profile) {
        ValidationUtil.requirePositiveId(userId, "userId");
        if (profile == null) {
            throw new IllegalArgumentException("profile must be provided");
        }

        User user = findById(userId);
        UserProfile existingProfile = userProfileRepository.findByUserId(userId).orElseGet(UserProfile::new);
        existingProfile.setBio(profile.getBio());
        existingProfile.setAffiliation(profile.getAffiliation());
        existingProfile.setPhotoUrl(profile.getPhotoUrl());
        existingProfile.setPhone(profile.getPhone());
        existingProfile.setResearchInterests(profile.getResearchInterests());
        existingProfile.setUser(user);
        user.setUserProfile(existingProfile);
        User savedUser = userRepository.save(user);
        return savedUser.getUserProfile();
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> findProfileByUserId(Long userId) {
        ValidationUtil.requirePositiveId(userId, "userId");
        return userProfileRepository.findByUserId(userId);
    }
}




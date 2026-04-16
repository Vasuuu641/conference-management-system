package com.vasu.conference_management.controller;

import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.UserProfile;
import com.vasu.conference_management.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Map<String, Object>> listUsers() {
        return userService.findAll().stream().map(this::toUserResponse).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        return toUserResponse(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        return toUserResponse(userService.findByUsername(username));
    }

    @GetMapping("/{id}/profile")
    public Map<String, Object> getProfile(@PathVariable Long id) {
        User user = userService.findById(id);
        UserProfile profile = userService.findProfileByUserId(id).orElse(null);
        return toProfileResponse(user.getId(), profile);
    }

    @PutMapping("/{id}/profile")
    public Map<String, Object> updateProfile(@PathVariable Long id,
                                             @RequestBody Map<String, String> request) {
        UserProfile profile = new UserProfile();
        profile.setBio(request.get("bio"));
        profile.setAffiliation(request.get("affiliation"));
        profile.setPhotoUrl(request.get("photoUrl"));
        profile.setPhone(request.get("phone"));
        profile.setResearchInterests(request.get("researchInterests"));

        UserProfile savedProfile = userService.upsertProfile(id, profile);
        return toProfileResponse(id, savedProfile);
    }

    private Map<String, Object> toUserResponse(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName() == null ? "" : user.getFirstName(),
                "lastName", user.getLastName() == null ? "" : user.getLastName(),
                "enabled", user.getEnabled() != null && user.getEnabled(),
                "roles", user.getRoles().stream().map(role -> role.getRoleName()).toList(),
                "createdDate", user.getCreatedDate() == null ? "" : user.getCreatedDate(),
                "updatedDate", user.getUpdatedDate() == null ? "" : user.getUpdatedDate()
        );
    }

    private Map<String, Object> toProfileResponse(Long userId, UserProfile profile) {
        if (profile == null) {
            return Map.of(
                    "userId", userId,
                    "bio", "",
                    "affiliation", "",
                    "photoUrl", "",
                    "phone", "",
                    "researchInterests", ""
            );
        }

        return Map.of(
                "userId", userId,
                "bio", profile.getBio() == null ? "" : profile.getBio(),
                "affiliation", profile.getAffiliation() == null ? "" : profile.getAffiliation(),
                "photoUrl", profile.getPhotoUrl() == null ? "" : profile.getPhotoUrl(),
                "phone", profile.getPhone() == null ? "" : profile.getPhone(),
                "researchInterests", profile.getResearchInterests() == null ? "" : profile.getResearchInterests()
        );
    }
}



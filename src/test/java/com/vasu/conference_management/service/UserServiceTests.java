package com.vasu.conference_management.service;

import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.UserProfile;
import com.vasu.conference_management.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void registerCreatesUserWithRole() {
        User user = new User();
        user.setUsername("service-user-1");
        user.setEmail("service-user-1@example.com");
        user.setPassword("secret");
        user.setFirstName("Service");
        user.setLastName("User");

        User saved = userService.register(user, Set.of("AUTHOR"));

        assertNotNull(saved.getId());
        assertEquals(1, saved.getRoles().size());
        assertTrue(saved.getRoles().stream().anyMatch(role -> "AUTHOR".equals(role.getRoleName())));
    }

    @Test
    void registerRejectsDuplicateUsername() {
        User first = new User();
        first.setUsername("service-user-dup");
        first.setEmail("service-user-dup-1@example.com");
        first.setPassword("secret");
        userService.register(first, Set.of("AUTHOR"));

        User second = new User();
        second.setUsername("service-user-dup");
        second.setEmail("service-user-dup-2@example.com");
        second.setPassword("secret");

        assertThrows(IllegalStateException.class, () -> userService.register(second, Set.of("AUTHOR")));
    }

    @Test
    void upsertProfileLinksProfileToUser() {
        User user = new User();
        user.setUsername("service-user-profile");
        user.setEmail("service-user-profile@example.com");
        user.setPassword("secret");
        User saved = userService.register(user, Set.of("REVIEWER"));

        UserProfile profile = new UserProfile();
        profile.setBio("Bio text");
        profile.setAffiliation("Test University");
        profile.setPhotoUrl("/img/profile.png");
        profile.setResearchInterests("Software Engineering");

        UserProfile stored = userService.upsertProfile(saved.getId(), profile);

        assertNotNull(stored.getId());
        assertEquals(saved.getId(), stored.getUser().getId());
        assertTrue(userProfileRepository.findByUserId(saved.getId()).isPresent());
    }
}


package com.vasu.conference_management.controller;

import com.vasu.conference_management.entity.Role;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.UserProfile;
import com.vasu.conference_management.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTests {

    private AutoCloseable mocks;
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void listUsersReturnsSanitizedUserFields() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("author1");
        user.setEmail("author1@example.com");
        user.setPassword("$2a$mock");
        user.setEnabled(true);
        user.setRoles(Set.of(new Role(1L, "AUTHOR", "Author role")));

        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("author1"))
                .andExpect(jsonPath("$[0].email").value("author1@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("AUTHOR"));
    }

    @Test
    void getProfileReturnsEmptyPayloadWhenProfileMissing() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setUsername("reviewer1");
        user.setEmail("reviewer@example.com");

        when(userService.findById(10L)).thenReturn(user);
        when(userService.findProfileByUserId(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/10/profile").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.bio").value(""))
                .andExpect(jsonPath("$.affiliation").value(""));
    }

    @Test
    void updateProfilePersistsFields() throws Exception {
        UserProfile saved = new UserProfile();
        saved.setBio("Distributed systems researcher");
        saved.setAffiliation("ABC University");
        saved.setPhotoUrl("/images/profile.jpg");
        saved.setPhone("12345");
        saved.setResearchInterests("Systems, ML");

        when(userService.upsertProfile(org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.any(UserProfile.class)))
                .thenReturn(saved);

        String payload = "{"
                + "\"bio\":\"Distributed systems researcher\","
                + "\"affiliation\":\"ABC University\","
                + "\"photoUrl\":\"/images/profile.jpg\","
                + "\"phone\":\"12345\","
                + "\"researchInterests\":\"Systems, ML\""
                + "}";

        mockMvc.perform(put("/api/users/5/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5L))
                .andExpect(jsonPath("$.bio").value("Distributed systems researcher"))
                .andExpect(jsonPath("$.affiliation").value("ABC University"));
    }
}



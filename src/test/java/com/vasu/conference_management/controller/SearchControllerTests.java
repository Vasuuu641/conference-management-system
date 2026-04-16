package com.vasu.conference_management.controller;

import com.vasu.conference_management.entity.Conference;
import com.vasu.conference_management.entity.Paper;
import com.vasu.conference_management.entity.Tag;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.service.PaperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTests {

    private AutoCloseable mocks;
    private MockMvc mockMvc;

    @Mock
    private PaperService paperService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new SearchController(paperService)).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void searchPapersReturnsFilteredResults() throws Exception {
        Conference conference = new Conference();
        conference.setId(2L);
        conference.setTitle("AI Summit");

        User author = new User();
        author.setId(3L);
        author.setUsername("alice");

        Tag tag = new Tag();
        tag.setId(8L);
        tag.setTagName("Machine Learning");

        Paper paper = new Paper();
        paper.setId(20L);
        paper.setTitle("Transformer Optimization");
        paper.setStatus(Paper.PaperStatus.UNDER_REVIEW);
        paper.setDecision("PENDING");
        paper.setConference(conference);
        paper.setAuthor(author);
        paper.setSubmissionDate(LocalDateTime.of(2026, 4, 16, 12, 0));
        paper.setTags(Set.of(tag));
        paper.setReviewers(Set.of());

        when(paperService.searchPapers("Transformer", 2L, Paper.PaperStatus.UNDER_REVIEW, "Machine", 3L))
                .thenReturn(List.of(paper));

        mockMvc.perform(get("/api/search/papers")
                        .param("title", "Transformer")
                        .param("conferenceId", "2")
                        .param("status", "UNDER_REVIEW")
                        .param("topic", "Machine")
                        .param("authorId", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].conferenceTitle").value("AI Summit"))
                .andExpect(jsonPath("$[0].authorUsername").value("alice"))
                .andExpect(jsonPath("$[0].tags[0]").value("Machine Learning"));
    }
}


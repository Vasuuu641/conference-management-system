package com.vasu.conference_management.config;

import com.vasu.conference_management.entity.*;
import com.vasu.conference_management.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               UserProfileRepository userProfileRepository,
                               ConferenceRepository conferenceRepository,
                               TagRepository tagRepository,
                               PaperRepository paperRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() > 0) {
                return;
            }

            Role adminRole = roleRepository.save(new Role(null, "ADMIN", "Conference chair/admin"));
            Role authorRole = roleRepository.save(new Role(null, "AUTHOR", "Paper author"));
            Role reviewerRole = roleRepository.save(new Role(null, "REVIEWER", "Paper reviewer"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@conference.local");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRoles(Set.of(adminRole));
            admin = userRepository.save(admin);

            User author = new User();
            author.setUsername("author1");
            author.setEmail("author1@conference.local");
            author.setPassword(passwordEncoder.encode("author123"));
            author.setFirstName("Alice");
            author.setLastName("Author");
            author.setRoles(Set.of(authorRole));
            author = userRepository.save(author);

            User reviewer = new User();
            reviewer.setUsername("reviewer1");
            reviewer.setEmail("reviewer1@conference.local");
            reviewer.setPassword(passwordEncoder.encode("review123"));
            reviewer.setFirstName("Bob");
            reviewer.setLastName("Reviewer");
            reviewer.setRoles(Set.of(reviewerRole));
            reviewer = userRepository.save(reviewer);

            userProfileRepository.save(new UserProfile(null, "Program chair", "University A", null, null, "Software engineering", admin));
            userProfileRepository.save(new UserProfile(null, "Researcher in AI", "University B", null, null, "Machine learning", author));
            userProfileRepository.save(new UserProfile(null, "Senior reviewer", "Institute C", null, null, "Distributed systems", reviewer));

            Conference conference = new Conference();
            conference.setTitle("International Conference on Software Engineering 2026");
            conference.setDescription("Sample seeded conference");
            conference.setStartDate(LocalDateTime.now().plusMonths(3));
            conference.setEndDate(LocalDateTime.now().plusMonths(3).plusDays(3));
            conference.setSubmissionDeadline(LocalDateTime.now().plusMonths(1));
            conference.setNotificationDate(LocalDateTime.now().plusMonths(2));
            conference.setLocation("Berlin");
            conference.setStatus(Conference.ConferenceStatus.OPEN);
            conference = conferenceRepository.save(conference);

            Tag ai = tagRepository.save(new Tag(null, "AI", "Artificial Intelligence"));
            Tag systems = tagRepository.save(new Tag(null, "Systems", "Computer Systems"));

            Paper paper = new Paper();
            paper.setTitle("A Practical Approach to Conference Management Systems");
            paper.setAbstractText("This is a sample seeded paper.");
            paper.setFilePath("/uploads/sample-paper.pdf");
            paper.setKeywords("conference management, spring boot");
            paper.setConference(conference);
            paper.setAuthor(author);
            paper.setTags(Set.of(ai, systems));
            paper.setReviewers(Set.of(reviewer));
            paper.setStatus(Paper.PaperStatus.UNDER_REVIEW);
            paper.setDecision("PENDING");
            paperRepository.save(paper);
        };
    }
}


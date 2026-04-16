package com.vasu.conference_management.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilTest {

    @Test
    void validateConferenceTimeline_acceptsValidOrder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(3);
        LocalDateTime start = now.plusDays(7);
        LocalDateTime end = now.plusDays(10);
        LocalDateTime notification = now.plusDays(5);

        assertDoesNotThrow(() -> DateUtil.validateConferenceTimeline(start, end, deadline, notification));
    }

    @Test
    void validateConferenceTimeline_rejectsInvalidOrder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(7);
        LocalDateTime end = now.plusDays(5);
        LocalDateTime deadline = now.plusDays(8);

        assertThrows(IllegalArgumentException.class,
                () -> DateUtil.validateConferenceTimeline(start, end, deadline, null));
    }

    @Test
    void isSubmissionWindowOpen_checksDeadline() {
        assertTrue(DateUtil.isSubmissionWindowOpen(LocalDateTime.now().plusHours(1)));
        assertFalse(DateUtil.isSubmissionWindowOpen(LocalDateTime.now().minusHours(1)));
    }
}


package com.vasu.conference_management.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilTest {

    @Test
    void requirePositiveId_rejectsInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requirePositiveId(0L, "paperId"));
    }

    @Test
    void requireNonEmpty_rejectsEmptyCollection() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonEmpty(Set.of(), "tagIds"));
    }

    @Test
    void normalizeRecommendation_standardizesValues() {
        assertEquals("WEAK_ACCEPT", ValidationUtil.normalizeRecommendation("weak accept"));
    }

    @Test
    void normalizeRecommendation_rejectsUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.normalizeRecommendation("MAYBE"));
    }
}


package com.vasu.conference_management.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUploadUtilTest {

    @Test
    void validateAndNormalizePaperPath_acceptsSupportedExtension() {
        String normalized = FileUploadUtil.validateAndNormalizePaperPath("uploads/paper-final.pdf");
        assertTrue(normalized.endsWith("paper-final.pdf"));
    }

    @Test
    void validateAndNormalizePaperPath_rejectsTraversal() {
        assertThrows(IllegalArgumentException.class,
                () -> FileUploadUtil.validateAndNormalizePaperPath("../secret/paper.pdf"));
    }

    @Test
    void generateStoredFilename_keepsExtension() {
        String filename = FileUploadUtil.generateStoredFilename("submission.docx");
        assertNotNull(filename);
        assertTrue(filename.endsWith(".docx"));
    }
}


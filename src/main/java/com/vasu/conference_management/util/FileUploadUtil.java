package com.vasu.conference_management.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class FileUploadUtil {
	private static final Set<String> ALLOWED_PAPER_EXTENSIONS = Set.of("pdf", "doc", "docx");

	private FileUploadUtil() {
	}

	public static String validateAndNormalizePaperPath(String filePath) {
		if (filePath == null || filePath.isBlank()) {
			return null;
		}

		String normalized = filePath.trim().replace('\\', '/');
		Path path = Paths.get(normalized).normalize();
		String normalizedText = path.toString().replace('\\', '/');

		if (normalizedText.contains("..")) {
			throw new IllegalArgumentException("filePath cannot contain path traversal segments");
		}

		if (!hasAllowedPaperExtension(normalizedText)) {
			throw new IllegalArgumentException("filePath must end with one of: .pdf, .doc, .docx");
		}

		return normalizedText;
	}

	public static String generateStoredFilename(String originalFilename) {
		String extension = extractExtension(originalFilename);
		if (!ALLOWED_PAPER_EXTENSIONS.contains(extension)) {
			throw new IllegalArgumentException("Unsupported file extension: " + extension);
		}
		return UUID.randomUUID() + "." + extension;
	}

	private static boolean hasAllowedPaperExtension(String filename) {
		return ALLOWED_PAPER_EXTENSIONS.contains(extractExtension(filename));
	}

	private static String extractExtension(String filename) {
		if (filename == null || filename.isBlank()) {
			return "";
		}
		int dot = filename.lastIndexOf('.');
		if (dot < 0 || dot == filename.length() - 1) {
			return "";
		}
		return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
	}
}


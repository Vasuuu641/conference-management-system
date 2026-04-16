package com.vasu.conference_management.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

public final class ValidationUtil {
	private static final Set<String> ALLOWED_RECOMMENDATIONS =
			Set.of("ACCEPT", "REJECT", "WEAK_ACCEPT", "WEAK_REJECT");

	private ValidationUtil() {
	}

	public static void requirePositiveId(Long id, String fieldName) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException(fieldName + " must be a positive number");
		}
	}

	public static void requireNonEmpty(Collection<?> values, String fieldName) {
		if (values == null || values.isEmpty()) {
			throw new IllegalArgumentException(fieldName + " must not be empty");
		}
	}

	public static String normalizeRecommendation(String recommendation) {
		if (recommendation == null || recommendation.isBlank()) {
			throw new IllegalArgumentException("recommendation is required");
		}

		String normalized = recommendation.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
		if (!ALLOWED_RECOMMENDATIONS.contains(normalized)) {
			throw new IllegalArgumentException("recommendation must be one of: " + ALLOWED_RECOMMENDATIONS);
		}
		return normalized;
	}
}


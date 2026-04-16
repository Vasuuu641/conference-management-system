package com.vasu.conference_management.util;

import java.time.LocalDateTime;

public final class DateUtil {

	private DateUtil() {
	}

	public static void validateConferenceTimeline(LocalDateTime startDate,
												  LocalDateTime endDate,
												  LocalDateTime submissionDeadline,
												  LocalDateTime notificationDate) {
		if (startDate == null || endDate == null || submissionDeadline == null) {
			throw new IllegalArgumentException("Conference dates must be provided");
		}

		if (!endDate.isAfter(startDate)) {
			throw new IllegalArgumentException("Conference endDate must be after startDate");
		}

		if (!submissionDeadline.isBefore(startDate)) {
			throw new IllegalArgumentException("submissionDeadline must be before conference startDate");
		}

		if (notificationDate != null && notificationDate.isBefore(submissionDeadline)) {
			throw new IllegalArgumentException("notificationDate must be on or after submissionDeadline");
		}
	}

	public static boolean isSubmissionWindowOpen(LocalDateTime submissionDeadline) {
		return submissionDeadline != null && !LocalDateTime.now().isAfter(submissionDeadline);
	}
}


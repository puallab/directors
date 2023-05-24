package com.directors.domain.schedule.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class InvalidMeetingTimeException extends RuntimeException {
	public static final String INVALID_START_TIME = "약속 시간은 현재 시간보다 이후 시간대여야 합니다.";
	private String userId;
	private String startTime;

	public InvalidMeetingTimeException(String message, LocalDateTime startTime, String userId) {
		super(message);
		this.userId = userId;
		this.startTime = startTime.toString();
	}

}

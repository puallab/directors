package com.directors.domain.schedule.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class InvalidMeetingRequest extends RuntimeException {
	public final static String CLOSED = "동일한 시각에 예약한 사람이 있습니다";
	private String userId;
	private String startTime;

	public InvalidMeetingRequest(String message, LocalDateTime startTime, String userId) {
		super(message);
		this.userId = userId;
		this.startTime = startTime.toString();
	}
}

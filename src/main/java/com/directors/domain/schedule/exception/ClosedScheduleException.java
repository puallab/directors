package com.directors.domain.schedule.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ClosedScheduleException extends RuntimeException {
	public static final String RESERVED_SCHEDULE = "해당 시간에 이미 예약한 사람이 있습니다.";
	private String userId;
	private String startTime;

	public ClosedScheduleException(LocalDateTime reservedTime, String userId) {
		super(RESERVED_SCHEDULE);
		this.startTime = reservedTime.toString();
		this.userId = userId;
	}

}

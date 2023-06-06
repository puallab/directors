package com.directors.domain.schedule.exception;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ClosedScheduleException extends RuntimeException {
	public static final String RESERVED_SCHEDULE = "디렉터가 허용한 시간대가 아닙니다.";
	private String userId;
	private String startTime;

	public ClosedScheduleException(LocalDateTime reservedTime, String userId) {
		super(RESERVED_SCHEDULE);
		this.startTime = reservedTime.toString();
		this.userId = userId;
	}

}

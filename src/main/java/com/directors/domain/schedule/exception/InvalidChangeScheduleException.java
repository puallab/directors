package com.directors.domain.schedule.exception;

import java.time.LocalDateTime;

import com.directors.domain.question.QuestionStatus;

import lombok.Getter;

@Getter
public class InvalidChangeScheduleException extends RuntimeException {
	public final static String INVALID_CHANGE = "해당 시간에 요청받은 질문이 있어 스케쥴의 상태를 변경할 수 없습니다.";
	private String startTime;
	private QuestionStatus status;

	public InvalidChangeScheduleException(String message, LocalDateTime startTime, QuestionStatus status) {
		super(message);
		this.startTime = startTime.toString();
		this.status = status;
	}
}

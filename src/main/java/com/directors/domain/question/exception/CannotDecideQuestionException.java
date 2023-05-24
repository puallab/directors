package com.directors.domain.question.exception;

import lombok.Getter;

@Getter
public class CannotDecideQuestionException extends RuntimeException {
	public static final String INVALID_DECIDE_AUTHORITY = "질문을 수락 및 거절 할 수있는 권한이 없습니다.";
	public static final String INVALID_FINISH_AUTHORITY = "약속 이행을 체크할 권한이 없습니다.";
	public final String questionId;

	public CannotDecideQuestionException(String message, String questionId) {
		super(message);
		this.questionId = questionId;
	}
}

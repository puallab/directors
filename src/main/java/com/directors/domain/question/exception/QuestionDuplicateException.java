package com.directors.domain.question.exception;

import lombok.Getter;

@Getter
public class QuestionDuplicateException extends RuntimeException {
	public static final String DUPLICATED = "동일한 디렉터와 진행중인 질문이 존재합니다. 질문을 수정하거나 삭제 후 다시 시도해주세요.";
	private String questionerId;

	public QuestionDuplicateException(String message, String questionerId) {
		super(message);
		this.questionerId = questionerId;
	}
}

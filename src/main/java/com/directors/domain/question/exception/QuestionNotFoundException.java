package com.directors.domain.question.exception;

import lombok.Getter;

@Getter
public class QuestionNotFoundException extends RuntimeException {
	public final static String NOTFOUND = "존재하지 않는 질문입니다.";
	private Long questionId;

	public QuestionNotFoundException(String message, Long questionId) {
		super(message);
		this.questionId = questionId;
	}
}

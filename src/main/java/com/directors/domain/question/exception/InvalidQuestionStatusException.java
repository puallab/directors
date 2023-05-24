package com.directors.domain.question.exception;

import com.directors.domain.question.QuestionStatus;

import lombok.Getter;

@Getter
public class InvalidQuestionStatusException extends RuntimeException {
	public final static String INVALID_STATUS = "대기 상태의 질문만 수정 가능합니다.";
	public final static String INVALID_FINISH = "진행중인 질문만 약속 이행 여부를 체크할 수 있습니다.";
	private Long questionId;
	private QuestionStatus questionStatus;

	public InvalidQuestionStatusException(String message, Long questionId, QuestionStatus questionStatus) {
		super(message);
		this.questionId = questionId;
		this.questionStatus = questionStatus;
	}
}

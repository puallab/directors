package com.directors.domain.question;

import java.time.LocalDateTime;

import com.directors.presentation.qeustion.request.CreateQuestionRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class Question {
	private Long id;
	private LocalDateTime createTime;
	private String title;
	private String content;
	private QuestionStatus status;
	private Boolean directorCheck;
	private Boolean questionCheck;
	private String questionerId;
	private String directorId;
	private String category; // 카테고리 결정되면 enum으로 변경 예정
	private Long scheduledId;
	private LocalDateTime startTime;

	public static Question of(CreateQuestionRequest createRequest, String questionerId, Long scheduledId) {
		return Question.builder()
			.title(createRequest.getTitle())
			.content(createRequest.getContent())
			.status(QuestionStatus.WAITING)
			.questionCheck(false)
			.directorCheck(false)
			.questionerId(questionerId)
			.directorId(createRequest.getDirectorId())
			.category(createRequest.getCategory())
			.scheduledId(scheduledId)
			.startTime(createRequest.getStartTime())
			.createTime(LocalDateTime.now())
			.build();
	}
}

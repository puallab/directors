package com.directors.infrastructure.jpa.question;

import java.time.LocalDateTime;

import com.directors.domain.question.QuestionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class QuestionSearchCondition {
	private String QuestionerId;
	private String directorId;
	private LocalDateTime startTime;
	private QuestionStatus status;
}

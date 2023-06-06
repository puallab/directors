package com.directors.infrastructure.jpa.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.directors.domain.question.Question;
import com.directors.domain.question.QuestionStatus;

public interface JpaQuestionRepository extends JpaRepository<Question, Long> {
	List<Question> findByDirectorId(String directorId);

	List<Question> findByQuestionerId(String questionerID);

	List<Question> findByQuestionerIdAndDirectorIdAndStatus(String questionerId, String directorId,
		QuestionStatus status);
}

package com.directors.domain.question;

import java.util.List;
import java.util.Optional;

import com.directors.infrastructure.jpa.question.QuestionSearchCondition;

public interface QuestionRepository {

	List<Question> findByDirectorId(String directorId);

	List<Question> findByQuestionerId(String questionerId);

	Question save(Question question);

	Optional<Question> findById(Long id);

	boolean existsQuestion(QuestionSearchCondition questionSearchCondition);

	List<Question> searchQuestion(QuestionSearchCondition condition);

}

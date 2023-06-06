package com.directors.infrastructure.jpa.question;

import static com.directors.domain.question.QQuestion.*;
import static com.directors.domain.schedule.QSchedule.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.directors.domain.question.Question;
import com.directors.domain.question.QuestionRepository;
import com.directors.domain.question.QuestionStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryAdapter implements QuestionRepository {
	private final JpaQuestionRepository jpaQuestionRepository;
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Question> findByDirectorId(String directorId) {
		return jpaQuestionRepository.findByDirectorId(directorId);
	}

	@Override
	public List<Question> findByQuestionerId(String questionerId) {
		return jpaQuestionRepository.findByQuestionerId(questionerId);
	}

	@Override
	public List<Question> findByQuestionerIdAndDirectorIdAndStatus(String questionerId, String directorId,
		QuestionStatus status) {
		return jpaQuestionRepository.findByQuestionerIdAndDirectorIdAndStatus(questionerId, directorId, status);
	}

	@Override
	public Question save(Question question) {
		return jpaQuestionRepository.save(question);
	}

	@Override
	public Optional<Question> findById(Long id) {
		return jpaQuestionRepository.findById(id);
	}

	@Override
	public List<Question> searchQuestion(QuestionSearchCondition condition) {
		return queryFactory
			.select(question)
			.from(question)
			.leftJoin(question.schedule, schedule).fetchJoin()
			.where(
				eqDirectorId(condition.getDirectorId()),
				eqQuestionerId(condition.getQuestionerId()),
				eqStartTime(condition.getStartTime()),
				eqStatus(condition.getStatus()))
			.fetch();
	}

	@Override
	public boolean existsQuestion(QuestionSearchCondition condition) {

		Integer fetchOne = queryFactory
			.selectOne()
			.from(question)
			.innerJoin(question.schedule, schedule)
			.where(
				eqDirectorId(condition.getDirectorId()),
				eqQuestionerId(condition.getQuestionerId()),
				eqStartTime(condition.getStartTime()),
				eqStatus(condition.getStatus()))
			.fetchFirst();

		return fetchOne != null;
	}

	private BooleanExpression eqQuestionerId(String questionerId) {
		return questionerId == null ? null : question.questioner.id.eq(questionerId);
	}

	private BooleanExpression eqDirectorId(String directorId) {
		return directorId == null ? null : question.director.id.eq(directorId);
	}

	private BooleanExpression eqStatus(QuestionStatus status) {
		return status == null ? null : question.status.eq(status);
	}

	private BooleanExpression eqStartTime(LocalDateTime startTime) {
		return startTime == null ? null : schedule.startTime.eq(startTime);
	}
}

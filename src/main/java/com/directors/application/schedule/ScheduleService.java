package com.directors.application.schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.directors.domain.question.QuestionRepository;
import com.directors.domain.question.QuestionStatus;
import com.directors.domain.schedule.Schedule;
import com.directors.domain.schedule.ScheduleRepository;
import com.directors.domain.schedule.ScheduleStatus;
import com.directors.domain.schedule.exception.InvalidChangeScheduleException;
import com.directors.domain.user.User;
import com.directors.domain.user.UserRepository;
import com.directors.domain.user.exception.NoSuchUserException;
import com.directors.infrastructure.jpa.question.QuestionSearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final QuestionRepository questionRepository;
	private final UserRepository userRepository;

	@Transactional
	public void open(String userId, List<LocalDateTime> startTimeList) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException(userId));

		List<Schedule> list = startTimeList.stream()
			.map(startTime -> {
				scheduleRepository.findByStartTimeAndUserId(startTime, userId)
					.ifPresent(schedule -> {
						checkQuestionForChangeSchedule(userId, startTime, QuestionStatus.CHATTING);

						schedule.closeSchedule();
					});

				return Schedule.of(startTime, ScheduleStatus.OPENED, user);
			})
			.collect(Collectors.toList());

		scheduleRepository.saveAll(list);
	}

	@Transactional
	public void close(String userId, List<LocalDateTime> startTimeList) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchUserException(userId));

		List<Schedule> list = startTimeList.stream()
			.map(startTime -> {
				scheduleRepository.findByStartTimeAndUserId(startTime, userId)
					.ifPresent(schedule -> {
						checkQuestionForChangeSchedule(userId, startTime, QuestionStatus.WAITING);

						schedule.closeSchedule();
					});

				return Schedule.of(startTime, ScheduleStatus.CLOSED, user);
			})
			.collect(Collectors.toList());

		scheduleRepository.saveAll(list);
	}

	private void checkQuestionForChangeSchedule(String userId, LocalDateTime startTime, QuestionStatus status) {
		boolean existsChattingQuestion = questionRepository.existsQuestion(
			QuestionSearchCondition.builder()
				.directorId(userId)
				.startTime(startTime)
				.status(status)
				.build());

		if (existsChattingQuestion) {
			throw new InvalidChangeScheduleException(InvalidChangeScheduleException.INVALID_CHANGE, startTime, status);
		}

	}
}

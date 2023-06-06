package com.directors.application.question;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.directors.domain.question.Question;
import com.directors.domain.question.QuestionRepository;
import com.directors.domain.question.QuestionStatus;
import com.directors.domain.question.exception.QuestionDuplicateException;
import com.directors.domain.question.exception.QuestionNotFoundException;
import com.directors.domain.schedule.Schedule;
import com.directors.domain.schedule.ScheduleRepository;
import com.directors.domain.schedule.exception.InvalidMeetingRequest;
import com.directors.domain.user.User;
import com.directors.domain.user.UserRepository;
import com.directors.domain.user.UserStatus;
import com.directors.domain.user.exception.NoSuchUserException;
import com.directors.infrastructure.jpa.question.QuestionSearchCondition;
import com.directors.presentation.question.request.CreateQuestionRequest;
import com.directors.presentation.question.request.DeclineQuestionRequest;
import com.directors.presentation.question.request.EditQuestionRequest;
import com.directors.presentation.question.response.DetailQuestionResponse;
import com.directors.presentation.question.response.ReceivedQuestionResponse;
import com.directors.presentation.question.response.SentQuestionResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final ScheduleRepository scheduleRepository;
	private final UserRepository userRepository;

	private static final String DECLINE_DEFAULT_MESSAGE = "다른 사용자의 질문이 채택되었습니다.";

	public List<SentQuestionResponse> getSendList(String questionerID) {
		List<Question> sentQuestions = questionRepository.findByQuestionerId(questionerID);
		return sentQuestions.stream()
			.map(question -> SentQuestionResponse.from(question))
			.toList();
	}

	public List<ReceivedQuestionResponse> getReceiveList(String directorId) {
		List<Question> receivedQuestions = questionRepository.findByDirectorId(directorId);
		return receivedQuestions.stream()
			.filter(question -> question.getStatus() != QuestionStatus.COMPLETE)
			.map(question -> ReceivedQuestionResponse.from(question))
			.toList();
	}

	@Transactional
	public void create(CreateQuestionRequest request, String questionerId) {
		//시간이 올바른지 확인, userId로부터 schedule 가져오기.
		long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

		//실험할 코드 추가
		Schedule schedule = validateTime(request.getStartTime(), request.getDirectorId());

		long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
		long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
		System.out.println("validate Schedule " + secDiffTime);

		beforeTime = System.currentTimeMillis();

		//동일한 디렉터에게 WAITTING 상태의 질문이 있다면 질문 불가능
		boolean isExists = questionRepository.existsQuestion(
			QuestionSearchCondition.builder()
				.QuestionerId(questionerId)
				.directorId(request.getDirectorId())
				.status(QuestionStatus.WAITING)
				.build());
		if (isExists) {
			throw new QuestionDuplicateException(QuestionDuplicateException.DUPLICATED, questionerId);
		}

		afterTime = System.currentTimeMillis();
		secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
		System.out.println("######exists question " + secDiffTime);

		beforeTime = System.currentTimeMillis();
		User director = getUserById(request.getDirectorId());
		User questioner = getUserById(questionerId);

		questioner.paymentReward();

		Question question = request.toQuestion(questioner, director, schedule);

		questionRepository.save(question);

		afterTime = System.currentTimeMillis();
		secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
		System.out.println("###### add exists question " + secDiffTime);
	}

	@Transactional
	public void edit(Long questionId, EditQuestionRequest editQuestionRequest) {
		Question question = getQuestionById(questionId);

		question.checkUneditableStatus();

		// 예약시간이 변경되었을 경우에 처리.
		boolean isChangedTime = question.isChangedTime(editQuestionRequest.getStartTime());

		if (isChangedTime) {
			// 변경하는 시간대 validation
			Schedule schedule = validateTime(editQuestionRequest.getStartTime(), editQuestionRequest.getDirectorId());
			question.changeSchedule(schedule);
		}

		question.editQuestion(editQuestionRequest.getTitle(), editQuestionRequest.getContent());
		questionRepository.save(question);
	}

	public DetailQuestionResponse getQuestionDetail(Long questionId) {
		Question question = getQuestionById(questionId);

		return DetailQuestionResponse.from(question);
	}

	@Transactional
	public void decline(Long questionId, String directorId, DeclineQuestionRequest declineQuestionRequest) {
		Question question = getQuestionById(questionId);

		question.decline(directorId, declineQuestionRequest.getComment());
	}

	@Transactional
	public void accept(Long questionId, String directorId) {

		Question question = getQuestionById(questionId);

		question.accept(directorId);

		//같은 시간에 들어온 질문 중 watting인거 모두 decline
		List<Question> questions = questionRepository.searchQuestion(
			QuestionSearchCondition.builder()
				.directorId(directorId)
				.startTime(question.getSchedule().getStartTime())
				.status(QuestionStatus.WAITING)
				.build());

		questions.stream().forEach(each -> {
			each.decline(directorId, DECLINE_DEFAULT_MESSAGE);
		});
	}

	@Transactional
	public void complete(Long questionId, String userId) {
		Question question = getQuestionById(questionId);

		question.meetingComplete(userId);
	}

	private Schedule validateTime(LocalDateTime startTime, String userId) {
		Schedule schedule = scheduleRepository.findByStartTimeAndUserId(startTime, userId)
			.orElseThrow(() -> new InvalidMeetingRequest(InvalidMeetingRequest.CLOSED, startTime, userId));

		schedule.checkChangeableScheduleTime();
		return schedule;
	}

	private User getUserById(String questionerId) {
		return userRepository.findByIdAndUserStatus(questionerId, UserStatus.JOINED)
			.orElseThrow(() -> new NoSuchUserException(questionerId));
	}

	private Question getQuestionById(Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new QuestionNotFoundException(QuestionNotFoundException.NOTFOUND, questionId));
	}
}
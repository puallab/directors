package com.directors.domain.question;

import java.time.LocalDateTime;

import com.directors.domain.common.BaseEntity;
import com.directors.domain.feedback.exception.CannotCreateFeedbackException;
import com.directors.domain.question.exception.CannotDecideQuestionException;
import com.directors.domain.question.exception.InvalidQuestionStatusException;
import com.directors.domain.room.exception.CannotCreateRoomException;
import com.directors.domain.schedule.Schedule;
import com.directors.domain.schedule.ScheduleStatus;
import com.directors.domain.schedule.exception.ClosedScheduleException;
import com.directors.domain.schedule.exception.InvalidMeetingTimeException;
import com.directors.domain.specialty.SpecialtyProperty;
import com.directors.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PUBLIC)
public class Question extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String content;
	@Enumerated(EnumType.STRING)
	private QuestionStatus status;
	private Boolean directorCheck;
	private Boolean questionCheck;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "questioner_id", referencedColumnName = "id")
	private User questioner;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "director_id", referencedColumnName = "id")
	private User director;

	@Enumerated(EnumType.STRING)
	private SpecialtyProperty category;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", referencedColumnName = "id")
	private Schedule schedule;
	private String comment;

	public void editQuestion(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void changeSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isNewQuestion() {
		return this.id == null;
	}

	public void checkUneditableStatus() {
		if (this.status != QuestionStatus.WAITING) {
			throw new InvalidQuestionStatusException(InvalidQuestionStatusException.INVALID_STATUS, this.id,
				this.status);
		}
	}

	public boolean isChangedTime(LocalDateTime startTime) {
		return this.schedule.equalsStartTime(startTime);
	}

	public void canCreateChatRoom(String directorId, LocalDateTime requestTime) {
		if (!this.director.getId().equals(directorId)) {
			throw new CannotCreateRoomException(this.id, CannotCreateRoomException.AUTH);
		}
		if (!status.equals(QuestionStatus.WAITING) || !(requestTime.isBefore(this.schedule.getStartTime()))) {
			throw new CannotCreateRoomException(this.id, CannotCreateRoomException.STATUS);
		}
	}

	public void changeQuestionStatusToChat() {
		this.status = QuestionStatus.CHATTING;
	}

	public void changeQuestionStatusToComplete() {
		this.status = QuestionStatus.COMPLETE;
	}

	public void canCreateFeedback(String questionerId) {
		if (!this.questioner.getId().equals(questionerId)) {
			throw new CannotCreateFeedbackException(this.id, CannotCreateFeedbackException.AUTH);
		}
		if (!this.status.equals(QuestionStatus.COMPLETE)) {
			throw new CannotCreateFeedbackException(this.id, CannotCreateFeedbackException.STATUS);
		}
	}

	public void decline(String directorId, String deniedComment) {
		canDecideQuestion(directorId);

		this.comment = deniedComment;
		this.status = QuestionStatus.COMPLETE;
	}

	public void accept(String directorId) {
		canDecideQuestion(directorId);
		validateStartTime();
		this.status = QuestionStatus.CHATTING;
	}

	private void canDecideQuestion(String directorId) {
		if (!this.director.getId().equals(directorId)) {
			throw new CannotDecideQuestionException(CannotDecideQuestionException.INVALID_DECIDE_AUTHORITY, directorId);
		}
	}

	private void validateStartTime() {
		if (this.schedule.getStatus().equals(ScheduleStatus.CLOSED)) {
			throw new ClosedScheduleException(this.schedule.getStartTime(), this.questioner.getId());
		}

		if (this.schedule.getStartTime().isBefore(LocalDateTime.now())) {
			//현재시간보다 이전이면 exception
			throw new InvalidMeetingTimeException(InvalidMeetingTimeException.INVALID_START_TIME,
				this.schedule.getStartTime(),
				questioner.getId());
		}
	}

	public void mettingCompleteChecking(String userId) {
		canFinishedQuestionStatus();
		checkComplete(userId);
	}

	private void checkComplete(String userId) {
		if (this.questioner.getId().equals(userId)) {
			questionCheck = true;
			return;
		}

		if (this.director.getId().equals(userId)) {
			directorCheck = true;
			return;
		}

		throw new CannotDecideQuestionException(CannotDecideQuestionException.INVALID_FINISH_AUTHORITY, userId);
	}

	private void canFinishedQuestionStatus() {
		if (this.status != QuestionStatus.CHATTING) {
			throw new InvalidQuestionStatusException(InvalidQuestionStatusException.INVALID_FINISH, this.id,
				this.status);
		}
	}

	public boolean isFinishedQuestion() {
		return this.questionCheck && this.directorCheck;
	}

}

package com.directors.domain.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.directors.domain.common.BaseEntity;
import com.directors.domain.question.Question;
import com.directors.domain.schedule.exception.InvalidMeetingRequest;
import com.directors.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Schedule extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime startTime;
	@Enumerated(EnumType.STRING)
	private ScheduleStatus status;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Question> questionList = new ArrayList<>();

	public static Schedule of(LocalDateTime startTime, ScheduleStatus status, User user) {
		return Schedule.builder()
			.startTime(startTime)
			.status(status)
			.user(user)
			.build();
	}

	public void checkChangeableScheduleTime() {
		if (this.status == ScheduleStatus.CLOSED) {
			throw new InvalidMeetingRequest(InvalidMeetingRequest.CLOSED, startTime, user.getId());
		}
	}

	public boolean equalsStartTime(LocalDateTime startTime) {
		return this.startTime.equals(startTime);
	}

	public void openSchedule() {
		if (this.status == ScheduleStatus.CLOSED) {
			this.status = this.status.changStatus();
		}
	}

	public void closeSchedule() {
		if (this.status == ScheduleStatus.OPENED) {
			this.status = this.status.changStatus();
		}
	}

	public boolean isNewSchedule() {
		return this.id == null;
	}

}

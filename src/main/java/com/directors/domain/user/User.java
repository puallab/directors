package com.directors.domain.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.directors.domain.common.BaseEntity;
import com.directors.domain.region.Address;
import com.directors.domain.region.Region;
import com.directors.domain.schedule.Schedule;
import com.directors.domain.specialty.Specialty;
import com.directors.domain.specialty.SpecialtyInfo;
import com.directors.domain.user.exception.NotEnoughRewardException;
import com.directors.domain.user.exception.UserRegionNotFoundException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseEntity {
	@Id
	private String id;

	private String password;

	private String name;

	private String nickname;

	private String email;

	private String phoneNumber;

	private Long reward;

	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	private LocalDateTime withdrawalDate;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Specialty> specialtyList = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Schedule> scheduleList = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "region_Id")
	private Region region;

	public Address getUserAddress() {
		if (region == null) {
			return null;
		}
		return region.getAddress();
	}

	public Region getRegion() {
		if (region == null) {
			throw new UserRegionNotFoundException(this.id);
		}
		return region;
	}

	public List<SpecialtyInfo> getSpecialtyInfoList() {
		if (specialtyList == null) {
			return new ArrayList<>();
		}
		return specialtyList
			.stream()
			.map(Specialty::getSpecialtyInfo)
			.collect(Collectors.toUnmodifiableList());
	}

	public List<LocalDateTime> getScheduleStartTimes() {
		if (scheduleList.isEmpty()) {
			return Collections.emptyList();
		}
		return scheduleList.stream()
			.map(Schedule::getStartTime)
			.collect(Collectors.toUnmodifiableList());
	}

	public void setPasswordByEncryption(String encryptedPassword) {
		this.password = encryptedPassword;
	}

	public void changeEmail(String newEmail) {
		this.email = newEmail;
	}

	public void withdrawal(LocalDateTime withdrawalDate) {
		this.userStatus = UserStatus.WITHDRAWN;
		this.withdrawalDate = withdrawalDate;
	}

	public void addReword() {
		this.reward += 1L;
	}

	public void paymentReward() {
		if (this.reward <= 0) {
			throw new NotEnoughRewardException(this.id);
		}
		this.reward -= 1L;
	}

	public void authenticateRegion(Region region) {
		this.region = region;
	}
}

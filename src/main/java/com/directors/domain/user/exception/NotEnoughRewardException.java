package com.directors.domain.user.exception;

public class NotEnoughRewardException extends RuntimeException {
	private final static String message = "리워드가 부족하여 질문할 수 없습니다.";
	public final String userId;

	public NotEnoughRewardException(String userId) {
		super(message);
		this.userId = userId;
	}
}

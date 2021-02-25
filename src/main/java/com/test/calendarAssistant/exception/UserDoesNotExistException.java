package com.test.calendarAssistant.exception;

public class UserDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The user doesn't exist";
	}
}
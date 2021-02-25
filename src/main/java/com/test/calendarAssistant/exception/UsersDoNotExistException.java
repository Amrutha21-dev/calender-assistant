package com.test.calendarAssistant.exception;

public class UsersDoNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "There are no users";
	}
}
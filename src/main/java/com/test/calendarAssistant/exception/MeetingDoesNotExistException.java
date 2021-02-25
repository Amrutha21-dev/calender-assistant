package com.test.calendarAssistant.exception;

public class MeetingDoesNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The meeting doesn't exist";
	}
}
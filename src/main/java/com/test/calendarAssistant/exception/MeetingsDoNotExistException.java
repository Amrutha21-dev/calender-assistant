package com.test.calendarAssistant.exception;

public class MeetingsDoNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "There are no meetings";
	}
}
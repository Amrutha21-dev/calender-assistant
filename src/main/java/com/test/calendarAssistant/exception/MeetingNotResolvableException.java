package com.test.calendarAssistant.exception;

public class MeetingNotResolvableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Meetings can't be resolved using this rule engine";
	}
	
	

}

package com.test.calendarAssistant.model;

import java.util.ArrayList;
import java.util.List;

public class Calendar {
	
	private int userId;
	private List<Meeting> meetings = new ArrayList<>();
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public List<Meeting> getMeetings() {
		return meetings;
	}
	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}
	
}

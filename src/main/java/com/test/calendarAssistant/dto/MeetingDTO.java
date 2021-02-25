package com.test.calendarAssistant.dto;

import java.util.Date;
import java.util.List;

public class MeetingDTO {

	private int id;
	private String name;
	private int organizerId;
	private List<Integer> invitees;
	private Date startTime;
	private Date endTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(int organizerId) {
		this.organizerId = organizerId;
	}
	public List<Integer> getInvitees() {
		return invitees;
	}
	public void setInvitees(List<Integer> invitees) {
		this.invitees = invitees;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}

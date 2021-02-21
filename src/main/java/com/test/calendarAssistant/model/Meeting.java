package com.test.calendarAssistant.model;

import java.util.Date;
import java.util.List;

public class Meeting implements Comparable<Meeting>{

		private int id;
		private String name;
		private int organizerId;
		private int numberOfInvitees;
		private List<User> invitees;
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
		public int getNumberOfInvitees() {
			return numberOfInvitees;
		}
		public void setNumberOfInvitees(int numberOfInvitees) {
			this.numberOfInvitees = numberOfInvitees;
		}
		public List<User> getInvitees() {
			return invitees;
		}
		public void setInvitees(List<User> invitees) {
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
		
		@Override
		public int compareTo(Meeting o) {
			if(this.getStartTime().before(o.getStartTime())) {
				return -1;
			}
			else if(this.getStartTime().after(o.getStartTime())) {
				return 1;
			}
			else {
				if(this.getEndTime().before(o.getEndTime())) {
					return -1;
				}
				else if(this.getEndTime().after(o.getEndTime())) {
					return 1;
				}
			}
			return 0;
		}
}

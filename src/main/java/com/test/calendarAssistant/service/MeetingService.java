package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.calendarAssistant.exception.MeetingNotResolvableException;
import com.test.calendarAssistant.exception.UserDoesNotExistException;
import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.model.User;

@Service
public class MeetingService {
	
	private static List<Meeting> meetings = new ArrayList<>();
	UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Meeting createMeeting(String name, Date startTime, Date endTime, int organizerId, List<Integer> idOfInvitees) {
		Meeting meeting = new Meeting();
		meeting.setId(meetings.size()+1);
		meeting.setName(name);
		meeting.setStartTime(startTime);
		meeting.setEndTime(endTime);
		meeting.setOrganizerId(organizerId);
		List<User> invitees = new ArrayList<>();
		for(Integer inviteeId:idOfInvitees) {
			try{
				User user = userService.getUser(inviteeId);
				if(user == null) {
					throw new UserDoesNotExistException();
				}
				invitees.add(user);
			}
			catch(UserDoesNotExistException e) {
				throw e;
			}
		}
		meeting.setInvitees(invitees);
		meetings.add(meeting);
		return meeting;
	}

	public Meeting resolve(Meeting firstMeeting, Meeting secondMeeting, int userId) {
		if(firstMeeting.getOrganizerId() == userId && secondMeeting.getOrganizerId() != userId) {
			return firstMeeting;
		}
		if(firstMeeting.getOrganizerId() != userId && secondMeeting.getOrganizerId() == userId) {
			return secondMeeting;
		}
		if(userService.getUser(firstMeeting.getOrganizerId()).getRank() < userService.getUser(secondMeeting.getOrganizerId()).getRank()) {
			return firstMeeting;
		}
		if(userService.getUser(firstMeeting.getOrganizerId()).getRank() > userService.getUser(secondMeeting.getOrganizerId()).getRank()) {
			return secondMeeting;
		}
		if(firstMeeting.getInvitees().size() > secondMeeting.getInvitees().size()) {
			return firstMeeting;
		}
		if(firstMeeting.getInvitees().size() < secondMeeting.getInvitees().size()) {
			return secondMeeting;
		}
		if(getMeetingRank(firstMeeting) < getMeetingRank(secondMeeting)) {
			return firstMeeting;
		}
		if(getMeetingRank(firstMeeting) > getMeetingRank(secondMeeting)) {
			return secondMeeting;
		}
		throw new MeetingNotResolvableException();
	}

	public float getMeetingRank(Meeting meeting) {
		int sum = 0;
		for(User i: meeting.getInvitees()) {
			sum+=i.getRank();
		}
		return (float)sum/(float)meeting.getInvitees().size();
	}
	
	//Acts as DAO getter
	public List<Meeting> getMeetings(){
		return meetings;
	}
	
	public Meeting getMeeting(int id){
		for(Meeting meeting : meetings) {
			if(meeting.getId() == id) {
				return meeting;
			}
		}
		return null;
	}

}

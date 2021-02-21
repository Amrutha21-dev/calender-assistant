package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.test.calendarAssistant.exception.MeetingNotResolvableException;
import com.test.calendarAssistant.model.Calendar;
import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.model.User;

public class MeetingService {
	
	public static Meeting createMeeting(int id, String name, Date startTime, Date endTime, int organizerId, List<Integer> idOfInvitees, int numberOfInvitees) {
		Meeting meeting = new Meeting();
		meeting.setId(id);
		meeting.setName(name);
		meeting.setStartTime(startTime);
		meeting.setEndTime(endTime);
		meeting.setOrganizerId(organizerId);
		List<User> invitees = new ArrayList<>();
		for(Integer inviteeId:idOfInvitees) {
			User user = UserService.getUser(inviteeId);
			invitees.add(user);
		}
		meeting.setInvitees(invitees);
		meeting.setNumberOfInvitees(numberOfInvitees);
		return meeting;
	}
	
	@SuppressWarnings("deprecation")
	public static Map<Integer, List<Integer>> createSchedule(List<Meeting> meetings){
		Map<Integer, List<Integer>> occupied = new TreeMap<>();
		int i=900;
		while(i<2000) {
			occupied.put(i, new ArrayList<Integer>());
			if(i%100 == 0) {
				i+=30;
			}
			else {
				i+=70;
			}
		}
		for(Meeting meeting:meetings) {
			int startKey = meeting.getStartTime().getHours()*100;
	        startKey += meeting.getStartTime().getMinutes();
	        int endKey = meeting.getEndTime().getHours()*100;
	        endKey += meeting.getEndTime().getMinutes();
	        i=startKey;
			while(i<endKey){
				List<Integer> meetingsOnSameTime = occupied.get(i);
				meetingsOnSameTime.add(meeting.getId());
				occupied.put(i, meetingsOnSameTime);
				if(i%100 == 0) {
					i+=30;
				}
				else {
					i+=70;
				}
			}
		}
		return occupied;
	}
	
	public static Set<Meeting> removeClashingMeetings(Calendar calendar) {
		List<Meeting> meetings = calendar.getMeetings();
		Set<Meeting> nonConflictingMeetings = new HashSet<>();
		nonConflictingMeetings.addAll(meetings);
		Set<Meeting> result = new HashSet<>();
		Map<Meeting, Set<Meeting>> conflictingMeetings = getConflictingMeetings(meetings);
		firstLoop : for(Meeting m:conflictingMeetings.keySet()) {
			nonConflictingMeetings.remove(m);
			if(result.contains(m)) {
				continue;
			}
			Meeting firstMeeting = m;
			for(Meeting i:conflictingMeetings.get(m)) {
				if(result.contains(i)) {
					continue firstLoop;
				}
				try {
					firstMeeting = resolve(firstMeeting,i,calendar.getUserId());
				}
				catch(MeetingNotResolvableException e) {
					System.out.println(e.getMessage());
					return null;
				}
			}
			result.add(firstMeeting);
		}
		result.addAll(nonConflictingMeetings);
		return result;
	}


	private static Meeting resolve(Meeting firstMeeting, Meeting secondMeeting, int userId) {
		if(firstMeeting.getOrganizerId() == userId && secondMeeting.getOrganizerId() != userId) {
			return firstMeeting;
		}
		if(firstMeeting.getOrganizerId() != userId && secondMeeting.getOrganizerId() == userId) {
			return secondMeeting;
		}
		if(UserService.getUser(firstMeeting.getOrganizerId()).getRank() < UserService.getUser(secondMeeting.getOrganizerId()).getRank()) {
			return firstMeeting;
		}
		if(UserService.getUser(firstMeeting.getOrganizerId()).getRank() > UserService.getUser(secondMeeting.getOrganizerId()).getRank()) {
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

	private static float getMeetingRank(Meeting meeting) {
		int sum = 0;
		for(User i: meeting.getInvitees()) {
			sum+=i.getRank();
		}
		return (float)sum/(float)meeting.getInvitees().size();
	}

	public static Map<Meeting, Set<Meeting>> getConflictingMeetings(List<Meeting> meetings) {
		Map<Meeting, Set<Meeting>> conflictingMeetings = new HashMap<>();
		Map<Integer, List<Integer>> occupied = createSchedule(meetings);
		for(Integer id:occupied.keySet()) {
			List<Integer> temp = occupied.get(id);
			if (temp.size()>1) {
				for(int i=0; i<temp.size(); i++) {
					Set<Meeting> confMeeting = conflictingMeetings.get(MeetingService.getMeeting(temp.get(i),meetings));
					if(confMeeting == null) {
						confMeeting = new HashSet<>();
					}
					for(int j=0;j<temp.size();j++) {
						if(i!=j) {
							confMeeting.add(MeetingService.getMeeting(temp.get(j),meetings));
						}
					}
					conflictingMeetings.put(MeetingService.getMeeting(temp.get(i),meetings),confMeeting);
				}
			}
		}
		return conflictingMeetings;
	}

	private static Meeting getMeeting(int id, List<Meeting> meetings) {
		for(Meeting meeting:meetings) {
			if(meeting.getId() == id) {
				return meeting;
			}
		}
		return null;
	}

	public static List<List<Integer>> getFreeSlots(List<Meeting> meetings) {
		Map<Integer, List<Integer>> occupied = createSchedule(meetings);
		List<List<Integer>> result = new ArrayList<>();
		List<Integer> temp = new ArrayList<>();
		for(Integer i:occupied.keySet()) {
			if(occupied.get(i).size() == 0) {
				if(temp.size() == 0) {
					temp.add(i);
				}
			}
			else if(temp.size() == 1) {
				temp.add(i);
				result.add(temp);
				temp = new ArrayList<>();
			}
		}
		if(temp.size() == 1) {
			temp.add(2000);
			result.add(temp);
		}
		int i=1;
		while(i<result.size()) {
			if(result.get(i).get(0).equals(result.get(i-1).get(1))) {
				result.get(i-1).set(1, result.get(i).get(1));
				result.remove(i);
			}
			else {
				i++;
			}
		}
		return result;
	}

	public static List<List<Integer>> getOverlappingFreeSlots(int duration, List<Meeting> meetings1,
			List<Meeting> meetings2) {
		List<List<Integer>> result = new ArrayList<>();
		Map<Integer, List<Integer>> schedule1 = createSchedule(meetings1);
		Map<Integer, List<Integer>> schedule2 = createSchedule(meetings2);
		List<Integer> temp = new ArrayList<>();
		for(Integer i:schedule1.keySet()) {
			if(schedule1.get(i).size() == 0 && schedule2.get(i).size() == 0) {
				if(temp.size() == 0) {
					temp.add(i);
				}
			}
			else {
				if(temp.size() != 0) {
					temp.add(i);
					result.add(temp);
					temp = new ArrayList<>();
				}
			}
		}
		if(temp.size() == 1) {
			temp.add(2000);
			result.add(temp);
		}
		Iterator<List<Integer>> i = result.iterator();
		while(i.hasNext()) {
			List<Integer> temp1 = i.next();
			if(temp1.get(1) - temp1.get(0) < duration) {
				i.remove();
			}
		}
		return result;
	}

}

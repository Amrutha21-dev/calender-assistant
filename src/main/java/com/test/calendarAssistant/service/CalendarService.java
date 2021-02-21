package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.test.calendarAssistant.model.Calendar;
import com.test.calendarAssistant.model.Meeting;

public class CalendarService {
	
	private static List<Calendar> calendars = new ArrayList<>();
	
	public static void add(int userId, String name, Date startTime, Date endTime, int organizerId, List<Integer> idOfInvitees, int numberOfInvitees) {
		Calendar calendar = null;
		Iterator<Calendar> i = calendars.iterator();
		while(i.hasNext()) {
			Calendar temp = i.next();
			if(temp.getUserId() == userId) {
				calendar = temp;
				i.remove();
			}
		}
		if(calendar == null) {
			calendar = new Calendar();
		}
		calendar.setUserId(userId);
		List<Meeting> meetings = calendar.getMeetings();
		if(meetings == null) {
			meetings = new ArrayList<>();
		}
		Meeting meeting = MeetingService.createMeeting(meetings.size()+1, name, startTime, endTime, organizerId, idOfInvitees, numberOfInvitees);
		meetings.add(meeting);
		Collections.sort(meetings);
		calendar.setMeetings(meetings);
		calendars.add(calendar);
	}
	
	//acts as DAO getter
	public static List<Calendar> getCalendars(){
		return calendars;
	}

	public static Map<Meeting, List<Meeting>> getConflictingMeetings(int userId) {
		for(Calendar calendar:calendars) {
			if(calendar.getUserId() == userId) {
				Map<Meeting, Set<Meeting>> conflictingMeetings = MeetingService.getConflictingMeetings(calendar.getMeetings()); 
				Map<Meeting, List<Meeting>> result = new HashMap<>();
				for(Meeting m:conflictingMeetings.keySet()) {
					Set<Meeting> setMeeting = conflictingMeetings.get(m);
					List<Meeting> listMeeting = new ArrayList<>();
					listMeeting.addAll(setMeeting);
					Collections.sort(listMeeting);
					result.put(m, listMeeting);
				}
				
				return result;
			}
		}
		return null;
	}
	
	public static List<Meeting> removeClashingMeetings(int userId) {
		for(Calendar calendar:calendars) {
			if(calendar.getUserId() == userId) {
				Set<Meeting> resolvedMeetings = MeetingService.removeClashingMeetings(calendar); 
				List<Meeting> result = new ArrayList<>();
				result.addAll(resolvedMeetings);
				Collections.sort(result);
				return result;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static List<List<Date>> getFreeSlots(List<Meeting> resolvedMeetings){
		List<List<Integer>> temp= MeetingService.getFreeSlots(resolvedMeetings);
		List<List<Date>> result = new ArrayList<>();;
		Date meetingDate = resolvedMeetings.get(0).getStartTime();
		for(List<Integer> list:temp) {
			List<Date> dateList = new ArrayList<>();
			for(Integer i:list) {
				Date date = (Date)meetingDate.clone();
				date.setHours(i/100);
				date.setMinutes(i%100);
				dateList.add(date);
			}
			result.add(dateList);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static List<List<Date>> getFreeSlotsForNMinMeetingBetween(int duration, int userId1, int userId2) {
		List<Meeting> meetings1 = removeClashingMeetings(userId1);
		List<Meeting> meetings2 = removeClashingMeetings(userId2);
		List<List<Integer>> temp = MeetingService.getOverlappingFreeSlots(duration, meetings1, meetings2);
		List<List<Date>> result = new ArrayList<>();
		Date meetingDate = meetings1.get(0).getStartTime();
		for(List<Integer> list:temp) {
			List<Date> dateList = new ArrayList<>();
			for(Integer i:list) {
				Date date = (Date)meetingDate.clone();
				date.setHours(i/100);
				date.setMinutes(i%100);
				dateList.add(date);
			}
			result.add(dateList);
		}
		return result;		
	}

}

package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.calendarAssistant.exception.MeetingNotResolvableException;
import com.test.calendarAssistant.model.Calendar;
import com.test.calendarAssistant.model.Meeting;

@Service
public class CalendarService {
	
	MeetingService meetingService;

	@Autowired
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	
	private static List<Calendar> calendars = new ArrayList<>();
	
	public Calendar add(int userId, int meetingId) {
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
		Meeting meeting = meetingService.getMeeting(meetingId);
		meetings.add(meeting);
		Collections.sort(meetings);
		calendar.setMeetings(meetings);
		calendars.add(calendar);
		return calendar;
	}
	
	//acts as DAO getter
	public List<Calendar> getCalendars(){
		return calendars;
	}
	
	public Calendar getCalendar(int userId) {
		for(Calendar calendar:calendars) {
			if(calendar.getUserId() == userId) {
				return calendar;
			}
		}
		return null;
	}

	public Map<Meeting, List<Meeting>> getConflictingMeetings(int userId) {
		for(Calendar calendar:calendars) {
			if(calendar.getUserId() == userId) {
				List<Meeting> meetings = calendar.getMeetings();
				Map<Meeting, Set<Meeting>> conflictingMeetings = new HashMap<>();
				Map<Integer, List<Integer>> occupied = createSchedule(meetings);
				for(Integer id:occupied.keySet()) {
					List<Integer> temp = occupied.get(id);
					if (temp.size()>1) {
						for(int i=0; i<temp.size(); i++) {
							Set<Meeting> confMeeting = conflictingMeetings.get(meetingService.getMeeting(temp.get(i)));
							if(confMeeting == null) {
								confMeeting = new HashSet<>();
							}
							for(int j=0;j<temp.size();j++) {
								if(i!=j) {
									confMeeting.add(meetingService.getMeeting(temp.get(j)));
								}
							}
							conflictingMeetings.put(meetingService.getMeeting(temp.get(i)),confMeeting);
						}
					}
				}
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
	
	public List<Meeting> removeClashingMeetings(int userId) {
		for(Calendar calendar:calendars) {
			if(calendar.getUserId() == userId) {
				List<Meeting> meetings = calendar.getMeetings();
				Set<Meeting> nonConflictingMeetings = new HashSet<>();
				nonConflictingMeetings.addAll(meetings);
				Set<Meeting> result = new HashSet<>();
				Map<Meeting, List<Meeting>> conflictingMeetings = getConflictingMeetings(userId);
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
							firstMeeting = meetingService.resolve(firstMeeting,i,calendar.getUserId());
						}
						catch(MeetingNotResolvableException e) {
							System.out.println(e.getMessage());
							return null;
						}
					}
					result.add(firstMeeting);
				}
				result.addAll(nonConflictingMeetings);
				List<Meeting> listedResult = new ArrayList<>();
				listedResult.addAll(result);
				Collections.sort(listedResult);
				return listedResult;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public List<List<Date>> getFreeSlots(int userId){
		List<Meeting> resolvedMeetings = removeClashingMeetings(userId);
		Date meetingDate = resolvedMeetings.get(0).getStartTime();
		Map<Integer, List<Integer>> occupied = createSchedule(resolvedMeetings);
		List<List<Date>> result = new ArrayList<>();
		List<Date> temp = new ArrayList<>();
		for(Integer i:occupied.keySet()) {
			if(occupied.get(i).size() == 0) {
				if(temp.size() == 0) {
					Date date = (Date)meetingDate.clone();
					date.setHours(i/100);
					date.setMinutes(i%100);
					temp.add(date);
				}
			}
			else if(temp.size() == 1) {
				Date date = (Date)meetingDate.clone();
				date.setHours(i/100);
				date.setMinutes(i%100);
				temp.add(date);
				result.add(temp);
				temp = new ArrayList<>();
			}
		}
		if(temp.size() == 1) {
			Date date = (Date)meetingDate.clone();
			date.setHours(20);
			date.setMinutes(0);
			temp.add(date);
			result.add(temp);
		}
		int i=1;
		while(i<result.size()) {
			if(isEquals(result.get(i).get(0),result.get(i-1).get(1))) {
				result.get(i-1).set(1, result.get(i).get(1));
				result.remove(i);
			}
			else {
				i++;
			}
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	private boolean isEquals(Date date1, Date date2) {
		if(date1.getHours() == date2.getHours() && date1.getMinutes() == date2.getMinutes()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public List<List<Date>> getFreeSlotsForNMinMeetingBetween(int duration, int userId1, int userId2) {
		List<Meeting> meetings1 = removeClashingMeetings(userId1);
		List<Meeting> meetings2 = removeClashingMeetings(userId2);
		List<List<Integer>> temp = getOverlappingFreeSlots(duration, meetings1, meetings2);
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
	
	@SuppressWarnings("deprecation")
	private Map<Integer, List<Integer>> createSchedule(List<Meeting> meetings){
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
	

	private List<List<Integer>> getOverlappingFreeSlots(int duration, List<Meeting> meetings1,
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

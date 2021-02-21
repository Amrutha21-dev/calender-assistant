package com.test.calendarAssistant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.service.CalendarService;
import com.test.calendarAssistant.service.UserService;

public class Application {
	
	public static void main(String[] args) throws ParseException {
		System.out.println("Calendar Assistant using complex engine");
		System.out.println("Working hours are 09:00 to 20:00");
		System.out.println("Meetings can start at 0th minute or 30th minute of the hour");
		System.out.println("Meeting duration can only be in multiples of 30 minutes");
		System.out.println("Adding users");
        UserService.add("Thomas", "Mathew", 1);
        UserService.add("Jayaprakash", "Ganta", 1);
        UserService.add("Vikas", "KR", 2);
        UserService.add("Ashish", "Sodhi", 2);
        UserService.add("Vijay", "Prakash", 1);
        System.out.println("Adding Meetings to Calendar of user 1");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        CalendarService.add(1, "Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime, 1, Arrays.asList(1, 2), 2);
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        CalendarService.add(1, "Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime, 2, Arrays.asList(1, 2), 2);
        // third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        CalendarService.add(1, "Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2), 2);
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        CalendarService.add(1, "Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3), 2);
        // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        CalendarService.add(1, "Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3), 2);
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        CalendarService.add(1, "Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4), 3);
        // seventh meeting - no conflicts
        Date seventhMeetingStartTime = format.parse("2021-02-22 11:00");
        Date seventhMeetingEndTime = format.parse("2021-02-22 11:30");
        CalendarService.add(1, "Thomas-Ashish Meeting", seventhMeetingStartTime, seventhMeetingEndTime, 4, Arrays.asList(1, 4), 2);
        // eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        CalendarService.add(1, "Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4), 3);
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        CalendarService.add(1, "Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4), 3);
        System.out.println("Adding Meetings to Calendar of user 5");
        Date secondUserFirstMeetingStartTime = format.parse("2021-02-22 09:00");
        Date secondUserFirstMeetingEndTime = format.parse("2021-02-22 10:00");
        CalendarService.add(5, "Vijay First Meeting", secondUserFirstMeetingStartTime, secondUserFirstMeetingEndTime, 5, Arrays.asList(1, 5), 2);
        Date secondUserSecondMeetingStartTime = format.parse("2021-02-22 10:30");
        Date secondUserSecondMeetingEndTime = format.parse("2021-02-22 11:30");
        CalendarService.add(5, "Vijay Second Meeting", secondUserSecondMeetingStartTime, secondUserSecondMeetingEndTime, 5, Arrays.asList(1, 5), 2);
        Date secondUserThirdMeetingStartTime = format.parse("2021-02-22 12:30");
        Date secondUserThirdMeetingEndTime = format.parse("2021-02-22 13:30");
        CalendarService.add(5, "Vijay Third Meeting", secondUserThirdMeetingStartTime, secondUserThirdMeetingEndTime, 5, Arrays.asList(1, 5), 2);
        Date secondUserFourthMeetingStartTime = format.parse("2021-02-22 15:00");
        Date secondUserFourthMeetingEndTime = format.parse("2021-02-22 20:00");
        CalendarService.add(5, "Vijay Fourth Meeting", secondUserFourthMeetingStartTime, secondUserFourthMeetingEndTime, 5, Arrays.asList(1, 5), 2);
	
        System.out.println("The conflicting meetings for user 1 are");
        Map<Meeting, List<Meeting>> conflictingMeetings = CalendarService.getConflictingMeetings(1);
		if(conflictingMeetings != null) {
			for(Meeting m: conflictingMeetings.keySet()) {
				System.out.println(m.getId()+" "+m.getName()+" has conflicts with");
				for(Meeting j:conflictingMeetings.get(m)) {
					System.out.println(j.getId()+" "+j.getName());
				}
			}
		}
		System.out.println("The resolved meetings for user 1 are");
		List<Meeting> resolvedMeetings = CalendarService.removeClashingMeetings(1);
		if(resolvedMeetings != null) {
			for(Meeting meeting:resolvedMeetings) {
				System.out.print(meeting.getId()+" "+meeting.getName());
				System.out.println();
			}
		}
		if(!resolvedMeetings.isEmpty()) {
			System.out.println("Free slots after resolving meetings are");
			List<List<Date>> result = CalendarService.getFreeSlots(resolvedMeetings);
			for(List<Date> date:result) {
				System.out.print("Start time ");
				System.out.println(date.get(0).toString());
				System.out.print("End time ");
				System.out.println(date.get(1).toString());
			}
		}
		System.out.println("The resolved meetings for user 5 are");
		resolvedMeetings = CalendarService.removeClashingMeetings(5);
		if(resolvedMeetings != null) {
			for(Meeting meeting:resolvedMeetings) {
				System.out.print(meeting.getId()+" "+meeting.getName());
				System.out.println();
			}
		}
		if(!resolvedMeetings.isEmpty()) {
			System.out.println("Free slots after resolving meetings are");
			List<List<Date>> result = CalendarService.getFreeSlots(resolvedMeetings);
			for(List<Date> date:result) {
				System.out.print("Start time ");
				System.out.println(date.get(0).toString());
				System.out.print("End time ");
				System.out.println(date.get(1).toString());
			}
		}
		System.out.println("Free slots for a 30 min meeting between user 1 and 5 are");
		List<List<Date>> result = CalendarService.getFreeSlotsForNMinMeetingBetween(30,1,5);
		for(List<Date> date:result) {
			System.out.print("Start time ");
			System.out.println(date.get(0).toString());
			System.out.print("End time ");
			System.out.println(date.get(1).toString());
		}	
	}
}

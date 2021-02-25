package com.test.calendarAssistant.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.test.calendarAssistant.model.Calendar;
import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.service.CalendarService;
import com.test.calendarAssistant.service.MeetingService;
import com.test.calendarAssistant.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarServiceTest {
	
	@Test
	@Order(1)
	public void testGetCalendarWithNoCalendar() {
		CalendarService calendarService = new CalendarService();
		assertNull(calendarService.getCalendar(1));
	}
	
	@Test
	public void testAddCalendarAndGetCalendar() throws ParseException {
		CalendarService calendarService = new CalendarService();
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date meetingStartTime = format.parse("2021-02-22 18:00");
        Date meetingEndTime = format.parse("2021-02-22 19:00");
        List<Integer> meetingInvitees = new ArrayList<>();
        meetingInvitees.add(1);
        meetingInvitees.add(2);
        meetingService.createMeeting("Thomas-JP Meeting", meetingStartTime, 
        		meetingEndTime, 1, meetingInvitees);
        calendarService.setMeetingService(meetingService);
        calendarService.add(1, 1);
        Calendar calendar = calendarService.getCalendar(1);
        assertEquals(1,calendar.getUserId());
        assertEquals(1,calendar.getMeetings().size());
        for(Meeting meeting:calendar.getMeetings()) {
        	assertEquals(1,meeting.getId());
        }
        userService.getUsers().clear();
        meetingService.getMeetings().clear();
		calendarService.getCalendars().clear();
	}
	
	@Test
	public void testGetConflictingMeetings() throws ParseException {
		CalendarService calendarService = new CalendarService();
		UserService userService = new UserService();
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		calendarService.setMeetingService(meetingService);
        userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        meetingService.createMeeting("Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime, 1, Arrays.asList(1, 2));
        calendarService.add(1, 1);
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        meetingService.createMeeting("Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 2);
        // third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        meetingService.createMeeting("Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 3);
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        meetingService.createMeeting("Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 4);
        // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 5);
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4));
        calendarService.add(1, 6);
        // seventh meeting - no conflicts
        Date seventhMeetingStartTime = format.parse("2021-02-22 11:00");
        Date seventhMeetingEndTime = format.parse("2021-02-22 11:30");
        meetingService.createMeeting("Thomas-Ashish Meeting", seventhMeetingStartTime, seventhMeetingEndTime, 4, Arrays.asList(1, 4));
        calendarService.add(1, 7);
        // eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4));
        calendarService.add(1, 8);
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4));
        calendarService.add(1, 9);
        Map<Meeting, List<Meeting>> conflictingMeetings = calendarService.getConflictingMeetings(1);
        assertEquals(8,conflictingMeetings.keySet().size());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(5)).size());
        assertEquals(6,conflictingMeetings.get(meetingService.getMeeting(5)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(1)).size());
        assertEquals(2,conflictingMeetings.get(meetingService.getMeeting(1)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(2)).size());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(2)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(4)).size());
        assertEquals(3,conflictingMeetings.get(meetingService.getMeeting(4)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(6)).size());
        assertEquals(5,conflictingMeetings.get(meetingService.getMeeting(6)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(8)).size());
        assertEquals(9,conflictingMeetings.get(meetingService.getMeeting(8)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(9)).size());
        assertEquals(8,conflictingMeetings.get(meetingService.getMeeting(9)).get(0).getId());
        assertEquals(1,conflictingMeetings.get(meetingService.getMeeting(3)).size());
        assertEquals(4,conflictingMeetings.get(meetingService.getMeeting(3)).get(0).getId());
        userService.getUsers().clear();
        meetingService.getMeetings().clear();
		calendarService.getCalendars().clear();
	}
	
	@Test
	public void testGetResolvedMeetings() throws ParseException {
		CalendarService calendarService = new CalendarService();
		UserService userService = new UserService();
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		calendarService.setMeetingService(meetingService);
        userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        meetingService.createMeeting("Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime, 1, Arrays.asList(1, 2));
        calendarService.add(1, 1);
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        meetingService.createMeeting("Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 2);
        // third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        meetingService.createMeeting("Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 3);
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        meetingService.createMeeting("Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 4);
        // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 5);
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4));
        calendarService.add(1, 6);
        // seventh meeting - no conflicts
        Date seventhMeetingStartTime = format.parse("2021-02-22 11:00");
        Date seventhMeetingEndTime = format.parse("2021-02-22 11:30");
        meetingService.createMeeting("Thomas-Ashish Meeting", seventhMeetingStartTime, seventhMeetingEndTime, 4, Arrays.asList(1, 4));
        calendarService.add(1, 7);
        // eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4));
        calendarService.add(1, 8);
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4));
        calendarService.add(1, 9);
        List<Meeting> resolvedMeetings = calendarService.removeClashingMeetings(1);
        assertEquals(5,resolvedMeetings.size());
        assertEquals(8,resolvedMeetings.get(0).getId());
        assertEquals(7,resolvedMeetings.get(1).getId());
        assertEquals(6,resolvedMeetings.get(2).getId());
        assertEquals(3,resolvedMeetings.get(3).getId());
        assertEquals(1,resolvedMeetings.get(4).getId());
		userService.getUsers().clear();
        meetingService.getMeetings().clear();
		calendarService.getCalendars().clear();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetFreeSlots() throws ParseException {
		CalendarService calendarService = new CalendarService();
		UserService userService = new UserService();
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		calendarService.setMeetingService(meetingService);
        userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        meetingService.createMeeting("Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime, 1, Arrays.asList(1, 2));
        calendarService.add(1, 1);
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        meetingService.createMeeting("Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 2);
        // third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        meetingService.createMeeting("Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 3);
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        meetingService.createMeeting("Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 4);
        // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 5);
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4));
        calendarService.add(1, 6);
        // seventh meeting - no conflicts
        Date seventhMeetingStartTime = format.parse("2021-02-22 11:00");
        Date seventhMeetingEndTime = format.parse("2021-02-22 11:30");
        meetingService.createMeeting("Thomas-Ashish Meeting", seventhMeetingStartTime, seventhMeetingEndTime, 4, Arrays.asList(1, 4));
        calendarService.add(1, 7);
        // eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4));
        calendarService.add(1, 8);
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4));
        calendarService.add(1, 9);
		List<List<Date>> result = calendarService.getFreeSlots(1);
		assertEquals(5,result.size());
		assertEquals(10,result.get(0).get(0).getHours());
		assertEquals(0,result.get(0).get(0).getMinutes());
		assertEquals(11,result.get(0).get(1).getHours());
		assertEquals(0,result.get(0).get(1).getMinutes());
		assertEquals(11,result.get(1).get(0).getHours());
		assertEquals(30,result.get(1).get(0).getMinutes());
		assertEquals(14,result.get(1).get(1).getHours());
		assertEquals(30,result.get(1).get(1).getMinutes());
		assertEquals(15,result.get(2).get(0).getHours());
		assertEquals(0,result.get(2).get(0).getMinutes());
		assertEquals(16,result.get(2).get(1).getHours());
		assertEquals(0,result.get(2).get(1).getMinutes());
		assertEquals(17,result.get(3).get(0).getHours());
		assertEquals(0,result.get(3).get(0).getMinutes());
		assertEquals(18,result.get(3).get(1).getHours());
		assertEquals(0,result.get(3).get(1).getMinutes());
		assertEquals(19,result.get(4).get(0).getHours());
		assertEquals(0,result.get(4).get(0).getMinutes());
		assertEquals(20,result.get(4).get(1).getHours());
		assertEquals(0,result.get(4).get(1).getMinutes());
		userService.getUsers().clear();
        meetingService.getMeetings().clear();
		calendarService.getCalendars().clear();
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetFreeSlotsForNMinMeetingBetween() throws ParseException {
		CalendarService calendarService = new CalendarService();
		UserService userService = new UserService();
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		calendarService.setMeetingService(meetingService);
        userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
        userService.add("Vijay", "Prakash", 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        meetingService.createMeeting("Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime, 1, Arrays.asList(1, 2));
        calendarService.add(1, 1);
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        meetingService.createMeeting("Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 2);
        // third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        meetingService.createMeeting("Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2));
        calendarService.add(1, 3);
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        meetingService.createMeeting("Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 4);
        // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3));
        calendarService.add(1, 5);
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4));
        calendarService.add(1, 6);
        // seventh meeting - no conflicts
        Date seventhMeetingStartTime = format.parse("2021-02-22 11:00");
        Date seventhMeetingEndTime = format.parse("2021-02-22 11:30");
        meetingService.createMeeting("Thomas-Ashish Meeting", seventhMeetingStartTime, seventhMeetingEndTime, 4, Arrays.asList(1, 4));
        calendarService.add(1, 7);
        // eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4));
        calendarService.add(1, 8);
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4));
        calendarService.add(1, 9);
        Date secondUserFirstMeetingStartTime = format.parse("2021-02-22 09:00");
        Date secondUserFirstMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Vijay First Meeting", secondUserFirstMeetingStartTime, secondUserFirstMeetingEndTime, 5, Arrays.asList(1, 5));
        calendarService.add(5, 10);
        Date secondUserSecondMeetingStartTime = format.parse("2021-02-22 10:30");
        Date secondUserSecondMeetingEndTime = format.parse("2021-02-22 11:30");
        meetingService.createMeeting("Vijay Second Meeting", secondUserSecondMeetingStartTime, secondUserSecondMeetingEndTime, 5, Arrays.asList(1, 5));
        calendarService.add(5, 11);
        Date secondUserThirdMeetingStartTime = format.parse("2021-02-22 12:30");
        Date secondUserThirdMeetingEndTime = format.parse("2021-02-22 13:30");
        meetingService.createMeeting("Vijay Third Meeting", secondUserThirdMeetingStartTime, secondUserThirdMeetingEndTime, 5, Arrays.asList(1, 5));
        calendarService.add(5, 12);
        Date secondUserFourthMeetingStartTime = format.parse("2021-02-22 15:00");
        Date secondUserFourthMeetingEndTime = format.parse("2021-02-22 20:00");
        meetingService.createMeeting("Vijay Fourth Meeting", secondUserFourthMeetingStartTime, secondUserFourthMeetingEndTime, 5, Arrays.asList(1, 5));
        calendarService.add(5, 13);
		List<List<Date>> result = calendarService.getFreeSlotsForNMinMeetingBetween(30,1,5);	
		assertEquals(3,result.size());
		assertEquals(10,result.get(0).get(0).getHours());
		assertEquals(0,result.get(0).get(0).getMinutes());
		assertEquals(10,result.get(0).get(1).getHours());
		assertEquals(30,result.get(0).get(1).getMinutes());
		assertEquals(11,result.get(1).get(0).getHours());
		assertEquals(30,result.get(1).get(0).getMinutes());
		assertEquals(12,result.get(1).get(1).getHours());
		assertEquals(30,result.get(1).get(1).getMinutes());
		assertEquals(13,result.get(2).get(0).getHours());
		assertEquals(30,result.get(2).get(0).getMinutes());
		assertEquals(14,result.get(2).get(1).getHours());
		assertEquals(30,result.get(2).get(1).getMinutes());
        userService.getUsers().clear();
        meetingService.getMeetings().clear();
		calendarService.getCalendars().clear();
	}

}

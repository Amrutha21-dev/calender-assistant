package com.test.calendarAssistant.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.test.calendarAssistant.exception.UserDoesNotExistException;
import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.service.MeetingService;
import com.test.calendarAssistant.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MeetingServiceTest {
	
	@Test
	@Order(1)
	public void testGetMeetings() {
		MeetingService meetingService = new MeetingService();
		assertEquals(0,meetingService.getMeetings().size());
	}
	
	@Test
	@Order(2)
	public void testCreateMeetingWithInvalidUser() throws ParseException {
		UserService userService = new UserService();
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date meetingStartTime = format.parse("2021-02-22 18:00");
        Date meetingEndTime = format.parse("2021-02-22 19:00");
        List<Integer> meetingInvitees = new ArrayList<>();
        meetingInvitees.add(1);
        meetingInvitees.add(2);
        assertThrows(UserDoesNotExistException.class,()->meetingService.createMeeting( 
        		"Thomas-JP Meeting", meetingStartTime, meetingEndTime, 1, meetingInvitees));
	}
	
	@Test
	@Order(3)
	public void testCreateMeetingAndGetMeeting() throws ParseException {
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
        Meeting meeting = meetingService.getMeeting(1);
        assertEquals(1,meeting.getId());
        assertEquals("Thomas-JP Meeting", meeting.getName());
        assertEquals(meetingStartTime, meeting.getStartTime());
        assertEquals(meetingEndTime, meeting.getEndTime());
        assertEquals(1, meeting.getOrganizerId());
        assertEquals(meetingInvitees.size(), meeting.getInvitees().size());
        for(int i=0;i<meetingInvitees.size();i++) {
        	assertEquals(meetingInvitees.get(i), meeting.getInvitees().get(i).getId());
        }
	}
	
	@Test
	@Order(4)
	public void testGetMeetingRank() {
		MeetingService meetingService = new MeetingService();
		assertEquals(1,meetingService.getMeetingRank(meetingService.getMeeting(1)));
		meetingService.getMeetings().clear();
	}
	
	@Test
	public void testResolveCaseOne() throws ParseException {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// first meeting - self organized
        Date firstMeetingStartTime = format.parse("2021-02-22 18:00");
        Date firstMeetingEndTime = format.parse("2021-02-22 19:00");
        meetingService.createMeeting("Thomas-JP Meeting", firstMeetingStartTime, firstMeetingEndTime,
        		1, Arrays.asList(1, 2));
        // second meeting - other organizer
        Date secondMeetingStartTime = format.parse("2021-02-22 18:30");
        Date secondMeetingEndTime = format.parse("2021-02-22 19:30"); 
        meetingService.createMeeting("Thomas-JP Meeting", secondMeetingStartTime, secondMeetingEndTime,
        		2, Arrays.asList(1, 2));
        assertEquals(meetingService.getMeeting(1), meetingService.resolve(meetingService.getMeeting(1), 
        		meetingService.getMeeting(2), 1));
		meetingService.getMeetings().clear();
		userService.getUsers().clear();
	}
	
	@Test
	public void testResolveCaseTwo() throws ParseException {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// third meeting - other organized
        Date thirdMeetingStartTime = format.parse("2021-02-22 16:00");
        Date thirdMeetingEndTime = format.parse("2021-02-22 17:00");
        meetingService.createMeeting("Thomas-JP Meeting", thirdMeetingStartTime, thirdMeetingEndTime, 2, Arrays.asList(1, 2));
        // fourth meeting - other organizer
        Date fourthMeetingStartTime = format.parse("2021-02-22 16:30");
        Date fourthMeetingEndTime = format.parse("2021-02-22 17:30");
        meetingService.createMeeting("Thomas-Vikas Meeting", fourthMeetingStartTime, fourthMeetingEndTime, 3, Arrays.asList(1, 3));
        assertEquals(meetingService.getMeeting(1), meetingService.resolve(meetingService.getMeeting(1), 
        		meetingService.getMeeting(2), 1));
		meetingService.getMeetings().clear();
		userService.getUsers().clear();
	}
	
	@Test
	public void testResolveCaseThree() throws ParseException {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		 // fifth meeting - other organizer with two invitees
        Date fifthMeetingStartTime = format.parse("2021-02-22 14:00");
        Date fifthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas Meeting", fifthMeetingStartTime, fifthMeetingEndTime, 3, Arrays.asList(1, 3));
        // sixth meeting - other organizer with three invitees
        Date sixthMeetingStartTime = format.parse("2021-02-22 14:30");
        Date sixthMeetingEndTime = format.parse("2021-02-22 15:00");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", sixthMeetingStartTime, sixthMeetingEndTime, 4, Arrays.asList(1, 3, 4));
        assertEquals(meetingService.getMeeting(2), meetingService.resolve(meetingService.getMeeting(1), 
        		meetingService.getMeeting(2), 1));
		meetingService.getMeetings().clear();
		userService.getUsers().clear();
	}
	
	@Test
	public void testResolveCaseFour() throws ParseException {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
        userService.add("Jayaprakash", "Ganta", 1);
        userService.add("Vikas", "KR", 2);
        userService.add("Ashish", "Sodhi", 2);
		MeetingService meetingService = new MeetingService();
		meetingService.setUserService(userService);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// eigth meeting - other organizer, same rank organizer but average of invitees is diff
        Date eigthMeetingStartTime = format.parse("2021-02-22 09:00");
        Date eigthMeetingEndTime = format.parse("2021-02-22 10:00");
        meetingService.createMeeting("Thomas-Ashish-JP Meeting", eigthMeetingStartTime, eigthMeetingEndTime, 4, Arrays.asList(1, 2, 4));
        // ninth meeting - other organizer, same rank organizer but average of invitees is diff
        Date ninthMeetingStartTime = format.parse("2021-02-22 09:30");
        Date ninthMeetingEndTime = format.parse("2021-02-22 10:30");
        meetingService.createMeeting("Thomas-Vikas-Ashish Meeting", ninthMeetingStartTime, ninthMeetingEndTime, 3, Arrays.asList(1, 3, 4));
        assertEquals(meetingService.getMeeting(1), meetingService.resolve(meetingService.getMeeting(1), 
        		meetingService.getMeeting(2), 1));
		meetingService.getMeetings().clear();
		userService.getUsers().clear();
	}
}

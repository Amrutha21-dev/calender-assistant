package com.test.calendarAssistant.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.calendarAssistant.dto.MeetingDTO;
import com.test.calendarAssistant.dto.ResolveMeetingDTO;
import com.test.calendarAssistant.exception.MeetingDoesNotExistException;
import com.test.calendarAssistant.exception.MeetingsDoNotExistException;
import com.test.calendarAssistant.model.Meeting;
import com.test.calendarAssistant.service.MeetingService;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
	
	@Autowired
	MeetingService meetingService;
	
	@PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Meeting> add(@RequestBody MeetingDTO meetingDTO) {
		Meeting meeting = meetingService.createMeeting(meetingDTO.getName(), meetingDTO.
				getStartTime(), meetingDTO.getEndTime(), meetingDTO.getOrganizerId(), 
				meetingDTO.getInvitees());
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(meeting);
	}
	
	@PostMapping(path = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Meeting>> addAll(@RequestBody List<MeetingDTO> meetingDTOs) {
		List<Meeting> meetings = new ArrayList<>();
		for(MeetingDTO meetingDTO : meetingDTOs) {
			Meeting meeting = meetingService.createMeeting(meetingDTO.getName(), meetingDTO.
				getStartTime(), meetingDTO.getEndTime(), meetingDTO.getOrganizerId(), 
				meetingDTO.getInvitees());
			meetings.add(meeting);
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(meetings);
	}
	
	@GetMapping(path = "/getRank/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Float> getRank(@PathVariable("id") int meetingId) {
		Meeting meeting = meetingService.getMeeting(meetingId);
		float rank = meetingService.getMeetingRank(meeting);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(rank);
	}
	
	@GetMapping(path = "/resolve", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Meeting> resolve(@RequestBody ResolveMeetingDTO resolveMeetingDTO) {
		Meeting meeting = meetingService.resolve(meetingService.getMeeting(resolveMeetingDTO
				.getMeetingId1()), meetingService.getMeeting(resolveMeetingDTO.getMeetingId2()),
				resolveMeetingDTO.getOrganizerId());
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(meeting);
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/get/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity get(@PathVariable int id) {
		try{
			Meeting meeting = meetingService.getMeeting(id);
			if(meeting == null) {
				throw new MeetingDoesNotExistException();
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(meeting);
		}
		catch(MeetingDoesNotExistException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getAll", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getAll() {
		try {
			List<Meeting> users = meetingService.getMeetings();
			if(users == null || users.isEmpty()) {
				throw new MeetingsDoNotExistException();
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(users);
		}
		catch(MeetingsDoNotExistException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}

}

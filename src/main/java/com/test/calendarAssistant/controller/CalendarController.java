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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.calendarAssistant.dto.CalendarDTO;
import com.test.calendarAssistant.model.Calendar;
import com.test.calendarAssistant.service.CalendarService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
	
	@Autowired
	CalendarService calendarService;
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/get/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity get(@PathVariable int id) {
		Calendar calendar = calendarService.getCalendar(id);
		if(calendar == null) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("");
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(calendar);
	}
	
	@PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Calendar> add(@RequestBody CalendarDTO calendarDTO) {
		Calendar calendar = calendarService.add(calendarDTO.getUserId(),calendarDTO.getMeetingId());
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(calendar);
	}
	
	@PostMapping(path = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<Calendar>> addAll(@RequestBody List<CalendarDTO> calendarDTOs) {
		List<Calendar> calendars = new ArrayList<>();
		for(CalendarDTO calendarDTO:calendarDTOs) {
			Calendar calendar = calendarService.add(calendarDTO.getUserId(),calendarDTO.getMeetingId());
			calendars.add(calendar);
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(calendars);
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getConflictingMeetings/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getConflictingMeetings(@PathVariable int id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(calendarService.getConflictingMeetings(id));
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getResolvedMeetings/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getResolvedMeetings(@PathVariable int id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(calendarService.removeClashingMeetings(id));
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getFreeSlots/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getFreeSlots(@PathVariable int id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(calendarService.getFreeSlots(id));
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getFreeSlotForMeeting", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getFreeSlotsForMeeting(@RequestParam("duration") int duration,
			@RequestParam("user2") int userId2, @RequestParam("user1") int userId1) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(calendarService.getFreeSlotsForNMinMeetingBetween(duration, userId1, userId2));
	}

}

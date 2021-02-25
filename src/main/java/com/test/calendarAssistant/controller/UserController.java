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

import com.test.calendarAssistant.exception.UserDoesNotExistException;
import com.test.calendarAssistant.exception.UsersDoNotExistException;
import com.test.calendarAssistant.model.User;
import com.test.calendarAssistant.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<User> add(@RequestBody User user) {
		user = userService.add(user.getFirstName(), user.getLastName(), user.getRank());
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(user);
	}
	
	@PostMapping(path = "/addAll", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<User>> addAll(@RequestBody List<User> users) {
		List<User> result = new ArrayList<>();
		for(User user:users) {
			user = userService.add(user.getFirstName(), user.getLastName(), user.getRank());
			result.add(user);
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(result);
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/get/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity get(@PathVariable int id) {
		try{
			User user = userService.getUser(id);
			if(user == null) {
				throw new UserDoesNotExistException();
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(user);
		}
		catch(UserDoesNotExistException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(path = "/getAll", produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity getAll() {
		try {
			List<User> users = userService.getUsers();
			if(users == null || users.isEmpty()) {
				throw new UsersDoNotExistException();
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(users);
		}
		catch(UsersDoNotExistException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}

}

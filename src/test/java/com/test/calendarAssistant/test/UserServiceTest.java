package com.test.calendarAssistant.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.test.calendarAssistant.model.User;
import com.test.calendarAssistant.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
	
	@Test
	@Order(1)
	public void testGetUsers() {
		UserService userService = new UserService();
		assertEquals(0,userService.getUsers().size());
	}
	
	@Test
	@Order(2)
	public void testGetUser() {
		UserService userService = new UserService();
		assertNull(userService.getUser(1));
	}
	
	@Test
	public void testAddUserAndGetUsers() {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
		List<User> users = userService.getUsers();
		assertEquals(1,users.size());
		for(User user:users) {
			assertEquals("Thomas",user.getFirstName());
			assertEquals("Mathew",user.getLastName());
			assertEquals(1,user.getRank());
		}
		userService.getUsers().clear();
	}
	
	@Test
	public void testAddUserAndGetUser() {
		UserService userService = new UserService();
		userService.add("Thomas", "Mathew", 1);
		User user = userService.getUser(1);
		assertEquals("Thomas",user.getFirstName());
		assertEquals("Mathew",user.getLastName());
		assertEquals(1,user.getRank());
		userService.getUsers().clear();
	}

}

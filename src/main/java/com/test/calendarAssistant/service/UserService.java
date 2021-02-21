package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.List;

import com.test.calendarAssistant.model.User;

public class UserService {
	
	private static List<User> users = new ArrayList<>();
	
	public static void add(String firstName, String lastName, int rank) {
		User user = new User();
		user.setId(users.size()+1);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRank(rank);
		users.add(user);
	}
	
	//Acts as DAO getter
	public static List<User> getUsers(){
		return users;
	}
	
	public static User getUser(int id){
		for(User user : users) {
			if(user.getId() == id) {
				return user;
			}
		}
		return null;
	}

}

package com.test.calendarAssistant.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.test.calendarAssistant.model.User;

@Service
public class UserService {
	
	private static List<User> users = new ArrayList<>();
	
	public User add(String firstName, String lastName, int rank) {
		User user = new User();
		user.setId(users.size()+1);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRank(rank);
		users.add(user);
		return user;
	}
	
	//Acts as DAO getter
	public List<User> getUsers(){
		return users;
	}
	
	public User getUser(int id){
		for(User user : users) {
			if(user.getId() == id) {
				return user;
			}
		}
		return null;
	}

}

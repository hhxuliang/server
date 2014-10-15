package com.way.chat.dao;

import java.util.ArrayList;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.User;

public interface UserDao {
	//ע��ɹ������û�id
	public int register(User u);

	public ArrayList<User> login(User u);
	public ArrayList<User> allUsers(User u);
	public boolean addFriends(AddNewFriendMsg a);
	public ArrayList<User> refresh(int id);
	public void logout(int id);
}

package com.way.chat.dao;

import java.util.ArrayList;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;

public interface UserDao {
	//注册成功返回用户id
	public int register(User u);

	public ArrayList<User> login(User u);
	public ArrayList<User> allUsers(User u);
	public boolean addFriends(AddNewFriendMsg a);
	public ArrayList<User> refresh(int id);
	public void logout(int id);
	public int saveMessageOnDB(TextMessage tm,int fromU,int toU);
	public boolean saveCrowdMessageOnDB(TextMessage tm,int fromU,int toU);
	public ArrayList<TranObject<TextMessage>> getOffLineMessage(int fromU, int toU);
	public ArrayList<TranObject<TextMessage>> getCrowdOffLineMessage(int fromU, int toU);
	public ArrayList<String> haveOffLineMess(User loginUser);
}

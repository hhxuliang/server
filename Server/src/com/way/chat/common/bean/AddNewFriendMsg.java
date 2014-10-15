package com.way.chat.common.bean;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import com.way.chat.server.OutputThread;

/**
 * ���������
 * 
 * @author liang
 */
public class AddNewFriendMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userID;
	private List<String> newFriends;

	public AddNewFriendMsg() {
		newFriends = new ArrayList<String>();
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String id) {
		this.userID = id;
	}
	
	public void addNewFriendID(String id) {
		this.newFriends.add(id);
	}
	
	public List<String> getFriends(){
		return newFriends;
	}
}

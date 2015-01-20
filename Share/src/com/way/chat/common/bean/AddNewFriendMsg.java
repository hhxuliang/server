package com.way.chat.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	public List<String> getFriends() {
		return newFriends;
	}

	@Override
	public String toString() {
		String js = null;
		js = "{\"AddNewFriendMsg\":\"not_support\"}";
		return js;
	}
}

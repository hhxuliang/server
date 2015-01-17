package com.way.chat.common.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * �û�����
 * 
 * @author way
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// QQ����
	private String loginAccount;
	private String mobile_NO;
	private String name;// �ǳ�
	private String email;// ����
	private String password;// ����
	private int isOnline;// �Ƿ�����
	private int img;// ͷ��ͼ��
	private String group;// ��һ������
	private String ip;
	private int port;
	private int iscrowd;
	private ArrayList<String> OffLineMessUser;

	public User() {
	}

	public User(int tid, String tname) {
		id = tid;
		name = tname;
		group = "�ҵĺ���";
		img = 0;
		isOnline = 1;

	}

	public ArrayList<String> getOffLineMessUser() {
		return OffLineMessUser;
	}

	public void setOffLineMessUser(ArrayList<String> id) {
		this.OffLineMessUser = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsCrowd() {
		return iscrowd;
	}

	public void setIsCrowd(int id) {
		this.iscrowd = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String s) {
		this.loginAccount = s;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			if (user.getId() == id && user.getIp().equals(ip)
					&& user.getPort() == port) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		String js = null;
		js = "{\"apiKey\":\"\",\"city\":\"\",\"latitude\":0,\"longitude\":0,\"province\":\"\",\"registerDate\":0,"
				+ "\"userAge\":0,\"userBirthday\":\"\",\"userDescription\":\"\",\"userHead\":\""
				+ img
				+ "\",\"userId\":\""
				+ id
				+ "\","
				+ "\"userName\":\""
				+ name
				+ "\",\"userNickname\":\""
				+ name
				+ "\",\"userPassword\":\"\",\"userPhone\":\"\",\"userQq\":\"\",\"userSex\":0,\"userState\":1}";
		return js;
	}

}

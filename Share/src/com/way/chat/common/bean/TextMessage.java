package com.way.chat.common.bean;

import java.io.Serializable;

/**
 * 锟侥憋拷锟斤拷息
 * 
 * @author way
 */
public class TextMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String datekey;
	private String serverdatekey;
	private String message;
	private int messageid;
	private int msgTpye;

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public String getServerdatekey() {
		return serverdatekey;
	}

	public void setServerdatekey(String serverdatekey) {
		this.serverdatekey = serverdatekey;
	}

	public String getDatekey() {
		return datekey;
	}

	public void setDatekey(String datekey) {
		this.datekey = datekey;
	}

	public TextMessage() {
		msgTpye = 0;
	}

	public TextMessage(String message) {
		this.message = message;
		msgTpye = 0;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getmsgtype() {
		return msgTpye;
	}

	public void setmsgtype(int p) {
		msgTpye = p;
	}

	@Override
	public String toString() {
		String js = null;
		String timesend="";
		if(datekey.length()>0)
		{
			timesend=datekey.substring(0, datekey.lastIndexOf(":"));
		}
		js = "{\"type\":"
				+ msgTpye
				+ ",\"timeSend\":\""+timesend+"\",\"location_x\":0,\"content\":\""
				+ message
				+ "\",\"fileSize\":0,\"toUserId\":\"2077\",\"location_y\":0,\"fromUserId\":\"2078\",\"timeLen\":0,\"serverdatekey\":\""
				+ datekey + "\"}";
		return js;
	}
}

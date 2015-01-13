package com.way.chat.common.tran.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 锟斤拷锟斤拷亩锟斤拷锟�,直锟斤拷通锟斤拷Socket锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
 * 
 * @author way
 */
public class TranObject<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TranObjectType type;// 锟斤拷锟酵碉拷锟斤拷息锟斤拷锟斤拷

	private int fromUser;// 锟斤拷锟斤拷锟侥革拷锟矫伙拷
	private int toUser;// 锟斤拷锟斤拷锟侥革拷锟矫伙拷
	private int crowd;
	private String fromusername;
	private int fromimg;

	private T object;// 锟斤拷锟斤拷亩锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟角匡拷锟斤拷锟皆讹拷锟斤拷锟轿猴拷
	private List<Integer> group;// 群锟斤拷锟斤拷锟斤拷些锟矫伙拷

	public TranObject(TranObjectType type) {
		this.type = type;
	}

	public String getFromUserName() {
		return fromusername;
	}

	public void setFromUserName(String fromUser) {
		this.fromusername = fromUser;
	}
	public int getFromImg() {
		return fromimg;
	}

	public void setFromImg(int from) {
		this.fromimg = from;
	}
	
	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}
	public int getCrowd() {
		return crowd;
	}

	public void setCrowd(int id) {
		this.crowd = id;
	}
	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public TranObjectType getType() {
		return type;
	}
	@Override
	public String toString() {
		return "";
	}
}

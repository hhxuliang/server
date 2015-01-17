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

	public String toString() {
		String ostr = "";
		if (object instanceof java.util.List) {
			ostr = "[";
			List<Object> lo = (List<Object>) object;
			for (Object o : lo) {
				if (ostr.length() > 1)
					ostr = ostr + ",";
				ostr = ostr + o.toString();
			}
			ostr = ostr + "]";
		} else
			ostr = object.toString();

		String js = "{\"TranObjectType\":" + type.ordinal() + "," + "\"fromUser\":"
				+ fromUser + "," + "\"toUser\":" + toUser + "," + "\"crowd\":"
				+ crowd + "," + "\"fromusername\":\"" + fromusername + "\","
				+ "\"fromimg\":" + fromimg + "," + "\"TranObject\":" + ostr
				+ "}";
		return js;
	}
}

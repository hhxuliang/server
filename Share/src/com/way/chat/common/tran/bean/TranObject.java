package com.way.chat.common.tran.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 传输的对象,直接通过Socket传输的最大对象
 * 
 * @author way
 */
public class TranObject implements Serializable
{
	/** */
	private static final long serialVersionUID = 8119073266400768336L;
	private TranObjectType type;// 发送的消息类型
	private long fromUser;// 来自哪个用户
	private List<Long> toUsers;// 发往哪个用户
	private Object tranValue;// 传输的对象，这个对象我们可以自定义任何

	public TranObject()
	{
	}
	
	public TranObject(TranObjectType type)
	{
		this.type = type;
	}

	public TranObjectType getType()
	{
		return type;
	}

	public void setType(TranObjectType type)
	{
		this.type = type;
	}

	public long getFromUser()
	{
		return fromUser;
	}

	public void setFromUser(long fromUser)
	{
		this.fromUser = fromUser;
	}

	public List<Long> getToUsers()
	{
		return toUsers;
	}

	public void setToUsers(List<Long> toUsers)
	{
		this.toUsers = toUsers;
	}

	public <T> T getTranValue(Class<T> clazz)
	{
		T rtnVal = null;
		if (clazz.isAssignableFrom(tranValue.getClass()))
		{
			rtnVal = clazz.cast(tranValue);
		}
		return rtnVal;
	}

	public void setTranValue(Object tranValue)
	{
		this.tranValue = tranValue;
	}
}

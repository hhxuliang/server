package com.way.chat.common.tran.bean;

import java.io.Serializable;
import java.util.List;

/**
 * ����Ķ���,ֱ��ͨ��Socket�����������
 * 
 * @author way
 */
public class TranObject implements Serializable
{
	/** */
	private static final long serialVersionUID = 8119073266400768336L;
	private TranObjectType type;// ���͵���Ϣ����
	private long fromUser;// �����ĸ��û�
	private List<Long> toUsers;// �����ĸ��û�
	private Object tranValue;// ����Ķ�������������ǿ����Զ����κ�

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

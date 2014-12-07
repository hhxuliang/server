package com.way.chat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.way.chat.entity.id.EntityId;

@Entity
@Table(name = "TOfflineMessage")
public class OfflineMessage extends EntityId
{
	/** */
	private static final long serialVersionUID = -6159031489201522054L;
	@Column(name = "LMsgFromId")
	private long msgFrom;
	@Column(name = "LMsgToId")
	private long msgTo;
	@Column(name = "TMsgTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date msgTime = null;

	public long getMsgFrom()
	{
		return msgFrom;
	}

	public void setMsgFrom(long msgFrom)
	{
		this.msgFrom = msgFrom;
	}

	public long getMsgTo()
	{
		return msgTo;
	}

	public void setMsgTo(long msgTo)
	{
		this.msgTo = msgTo;
	}

	public Date getMsgTime()
	{
		return msgTime;
	}

	public void setMsgTime(Date msgTime)
	{
		this.msgTime = msgTime;
	}

}

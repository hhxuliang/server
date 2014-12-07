package com.way.chat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.way.chat.entity.id.EntityId;

@Entity
@Table(name = "TComment")
public class Comment extends EntityId
{
	/** */
	private static final long serialVersionUID = 561196203074265868L;
	@Column(name = "SCommContent")
	private String commContent = "";
	@Column(name = "LCommOwnerId")
	private long commOwner;
	@Column(name = "LCommPicture")
	private long commPicture;
	@Column(name = "TCommTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date commTime = null;

	public String getCommContent()
	{
		return commContent;
	}

	public void setCommContent(String commContent)
	{
		this.commContent = commContent;
	}

	public Date getCommTime()
	{
		return commTime;
	}

	public void setCommTime(Date commTime)
	{
		this.commTime = commTime;
	}

	public long getCommOwner()
	{
		return commOwner;
	}

	public void setCommOwner(long commOwner)
	{
		this.commOwner = commOwner;
	}

	public long getCommPicture()
	{
		return commPicture;
	}

	public void setCommPicture(long commPicture)
	{
		this.commPicture = commPicture;
	}

}

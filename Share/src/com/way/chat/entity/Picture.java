package com.way.chat.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.way.chat.entity.id.EntityId;

@Entity
@Table(name = "TPicture")
public class Picture extends EntityId
{
	/** */
	private static final long serialVersionUID = -5025008814232252380L;
	@Column(name = "SPicUrl")
	private String picUrl = "";
	@Column(name = "LPicFromId")
	private long picFrom;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TPicUploadTime")
	private Date picUploadTime = null;

	public String getPicUrl()
	{
		return picUrl;
	}

	public void setPicUrl(String picUrl)
	{
		this.picUrl = picUrl;
	}

	public long getPicFrom()
	{
		return picFrom;
	}

	public void setPicFrom(long picFrom)
	{
		this.picFrom = picFrom;
	}

	public Date getPicUploadTime()
	{
		return picUploadTime;
	}

	public void setPicUploadTime(Date picUploadTime)
	{
		this.picUploadTime = picUploadTime;
	}

}

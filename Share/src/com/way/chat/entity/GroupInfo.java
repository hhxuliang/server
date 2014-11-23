package com.way.chat.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.way.chat.entity.id.EntityId;

@Entity
public class GroupInfo extends EntityId
{
	/** */
	private static final long serialVersionUID = 3743231387073457102L;
	@ManyToOne(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "QUserId", referencedColumnName = "UniqueId")
	private UserInfo userInfo;
	@Transient
	private Long userId = null;
	@Transient
	private String userName = "";
	@Lob
	@Column(name = "QFriends")
	private List<Long> friends = null;

	public UserInfo getUserInfo()
	{
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo)
	{
		this.userInfo = userInfo;
	}

	public Long getUserId()
	{
		if (userInfo != null)
		{
			userId = userInfo.getUniqueId();
		}
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public List<Long> getFriends()
	{
		return friends;
	}

	public void setFriends(List<Long> friends)
	{
		this.friends = friends;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupInfo other = (GroupInfo) obj;
		if (getUserId() == null)
		{
			if (other.getUserId() != null)
				return false;
		}
		else if (!getUserId().equals(other.getUserId()))
			return false;
		if (getName() == null)
		{
			if (other.getName() != null)
				return false;
		}
		else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

}

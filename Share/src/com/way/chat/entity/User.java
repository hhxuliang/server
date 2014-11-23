package com.way.chat.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.way.chat.entity.id.EntityId;

@Entity
@Table(name = "TUser")
public class User extends EntityId
{
	/** */
	private static final long serialVersionUID = -248171590900107556L;

	@Column(name = "SEmail", length = 32, unique = true, nullable = false)
	private String email = "";// 邮箱
	@Column(name = "SPassword", length = 32)
	private String password = "";// 密码
	@Column(name = "BOnline")
	private boolean online = false;// 是否在线
	@Column(name = "SIcon", length = 32)
	private String icon = "";// 头像图标
	@Column(name = "SClientAddr", length = 20)
	private String clientAddr = "";
	@Column(name = "IClientPort")
	private int clientPort = 0;
	@Column(name = "TCreateTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime = null;

	@OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "owner")
	private List<Group> groups = null;
	@Transient
	private List<Long> groupIds = null;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isOnline()
	{
		return online;
	}

	public void setOnline(boolean online)
	{
		this.online = online;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon(String image)
	{
		this.icon = image;
	}

	public String getClientAddr()
	{
		return clientAddr;
	}

	public void setClientAddr(String clientAddr)
	{
		this.clientAddr = clientAddr;
	}

	public int getClientPort()
	{
		return clientPort;
	}

	public void setClientPort(int clientPort)
	{
		this.clientPort = clientPort;
	}

	public List<Group> getGroups()
	{
		return groups;
	}

	public void setGroups(List<Group> groups)
	{
		this.groups = groups;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		User other = (User) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		return true;
	}

	public List<Long> getGroupIds()
	{
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds)
	{
		this.groupIds = groupIds;
	}
}

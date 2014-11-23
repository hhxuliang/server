package com.way.chat.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.way.chat.entity.id.EntityId;

@Entity
public class UserInfo extends EntityId
{
	/** */
	private static final long serialVersionUID = -248171590900107556L;

	@Column(name = "QAccount", length = 32)
	private String account = "";
	@Column(name = "QEmail", length = 32, unique = true, nullable = false)
	private String email = "";// 邮箱
	@Column(name = "QPassword", length = 32)
	private String password = "";// 密码
	@Column(name = "QOnline")
	private boolean online = false;// 是否在线
	@Column(name = "QImage", length = 32)
	private String image = "";// 头像图标
	@Column(name = "QClientAddr", length = 20)
	private String clientAddr = "";
	@Column(name = "QClientPort")
	private int clientPort = 0;

	@OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "userInfo")
	private List<GroupInfo> groups = null;

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

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

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
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

	public List<GroupInfo> getGroups()
	{
		return groups;
	}

	public void setGroups(List<GroupInfo> groups)
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
		UserInfo other = (UserInfo) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		return true;
	}
}

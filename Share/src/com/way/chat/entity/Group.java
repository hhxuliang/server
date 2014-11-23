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
public class Group extends EntityId
{
	/** */
	private static final long serialVersionUID = 3743231387073457102L;
	@ManyToOne(fetch = FetchType.LAZY, cascade = {})
	@JoinColumn(name = "LOwnerId", referencedColumnName = "LxId")
	private User owner;
	@Transient
	private Long ownerId = null;
	@Transient
	private String ownerName = "";
	@Lob
	@Column(name = "LMembers")
	private List<Long> members = null;
	@Column(name = "IType")
	private int type = 1;

	public User getOwner()
	{
		return owner;
	}

	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	public List<Long> getMembers()
	{
		return members;
	}

	public void setMembers(List<Long> members)
	{
		this.members = members;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public Long getOwnerId()
	{
		if (owner != null)
		{
			ownerId = owner.getXId();
		}
		return ownerId;
	}

	public void setOwnerId(Long ownerId)
	{
		this.ownerId = ownerId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		Group other = (Group) obj;
		if (owner == null)
		{
			if (other.owner != null)
				return false;
		}
		else if (!owner.equals(other.owner))
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

package com.way.chat.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

@MappedSuperclass
public class EntityId implements Serializable
{
	/** */
	private static final long serialVersionUID = -1972261441976802384L;
	@Id
	@Column(name = "UniqueId", nullable = false, updatable = false)
	@TableGenerator(name = "UniqueId", table = "UniqueIdFactory", allocationSize = 1, pkColumnName = "KeyName", valueColumnName = "KeyValue")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "UniqueId")
	private long uniqueId = 0L;// QQ∫≈¬Î
	@Column(name = "QName", length = 32)
	private String name = "";// Í«≥∆

	public long getUniqueId()
	{
		return uniqueId;
	}

	public void setUniqueId(long uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}

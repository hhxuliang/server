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
	@Column(name = "LxId", nullable = false, updatable = false)
	@TableGenerator(name = "LxId", table = "LxIdFactory", allocationSize = 1, pkColumnName = "ColName", valueColumnName = "ColValue")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LxId")
	private long id = 0L;// QQ∫≈¬Î
	@Column(name = "SName", length = 32)
	private String name = "";// Í«≥∆
	@Column(name = "SDescr", length = 64)
	private String descr = "";

	public long getId()
	{
		return id;
	}

	public void setId(long xId)
	{
		this.id = xId;
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

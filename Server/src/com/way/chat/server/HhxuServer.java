package com.way.chat.server;

import com.xinitren.jpa.EntityManagerWrapperFactory;

public interface HhxuServer
{

	public abstract void start();

	public abstract EntityManagerWrapperFactory getEntityManagerWrapperFactory();

	public abstract void setEntityManagerWrapperFactory(EntityManagerWrapperFactory entityManagerWrapperFactory);

}
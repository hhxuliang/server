package com.way.chat.server.impl;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.chat.entity.User;
import com.way.chat.server.HhxuServer;
import com.xinitren.jpa.EntityManagerWrapper;
import com.xinitren.jpa.EntityManagerWrapperFactory;

public class HhxuServerImpl extends IoHandlerAdapter implements Serializable, HhxuServer
{
	/** */
	private static final long serialVersionUID = 5736793209424785553L;
	private static final Logger logger = Logger.getLogger(HhxuServerImpl.class.getName());
	private IoAcceptor ioAcceptor = new NioSocketAcceptor();
	private EntityManagerWrapper entityManagerWrapper = null;
	private EntityManagerWrapperFactory entityManagerWrapperFactory = null;

	private ConcurrentMap<Long, IoSession> sessionMap = new ConcurrentHashMap<Long, IoSession>();

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		super.messageReceived(session, message);
		if (TranObject.class.isAssignableFrom(message.getClass()) == false)
		{
			return;
		}
		EntityManager em = entityManagerWrapper.getEntityManager();
		TranObject read = (TranObject) message;
		TranObject write = new TranObject();
		switch (read.getType())
		{
			case REGISTER:
			{
				User ui = read.getTranValue(User.class);
				if (ui != null)
				{
					String strTemp = "select u from " + entityManagerWrapper.getTableName(User.class) + " u where u.email = ?1";
					List<User> exists = entityManagerWrapper.query(em, User.class, strTemp, ui.getEmail());
					if (exists.isEmpty() == false)
					{
						write.setTranValue(new TextMessage("注册失败[" + ui.getEmail() + "]已经存在"));
					}
					else
					{
						entityManagerWrapper.persist(ui);
						write.setTranValue(ui);
					}
					write.setType(TranObjectType.REGISTER);
					session.write(write);
				}
				break;
			}
			case LOGIN:
			{
				User ui = read.getTranValue(User.class);
				try
				{
					List<User> users = entityManagerWrapper.query(em, User.class, "select u from " + entityManagerWrapper.getTableName(User.class) + " where (u.name = ?1 or u.email = ?2) and u.password = ?3", ui.getName(), ui.getEmail(), ui.getPassword());
					if (users.isEmpty())
					{
						TextMessage tm = new TextMessage("登录失败，请检查名称、邮件和密码是否正确");
						write.setTranValue(tm);
					}
					else
					{
						ui = users.get(0);
						ui.setOnline(true);
						ui.setClientAddr(session.getRemoteAddress().toString());
						ui = entityManagerWrapper.merge(ui);
					}
					session.write(write);
					sessionMap.put(ui.getId(), session);
				}
				catch (Exception ex)
				{
					logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				}
				break;
			}
			case LOGOUT:
			{
				User ui = read.getTranValue(User.class);
				sessionMap.remove(ui.getId());
				break;
			}
			case MESSAGE:
			{
				break;
			}
			case REFRESH:
			{
				break;
			}
			case ALLUSERS:
			{
				break;
			}
			case ADDFRIEND:
			{
				break;
			}
			default:
				break;
		}
		em.close();
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		super.sessionClosed(session);
	}

	@Override
	public void start()
	{
		try
		{
			entityManagerWrapper = entityManagerWrapperFactory.createEntityManagerWrapper(entityManagerWrapperFactory.getPersistUnitName(), entityManagerWrapperFactory.getConnMap());
			entityManagerWrapper.createTables();
			ioAcceptor.setHandler(this);
			ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
			ioAcceptor.getSessionConfig().setReadBufferSize(2048);
			ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			ioAcceptor.bind(new InetSocketAddress(Constants.SERVER_PORT));
		}
		catch (Exception ex)
		{
			logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		}
	}

	@Override
	public EntityManagerWrapperFactory getEntityManagerWrapperFactory()
	{
		return entityManagerWrapperFactory;
	}

	@Override
	public void setEntityManagerWrapperFactory(EntityManagerWrapperFactory entityManagerWrapperFactory)
	{
		this.entityManagerWrapperFactory = entityManagerWrapperFactory;
	}
}

package com.way.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.CommonMsg;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.MyDate;
import com.way.chat.dao.UserDao;
import com.way.chat.dao.impl.UserDaoFactory;

/**
 * 读消息线程和处理方法
 * 
 * @author way
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket对象
	private OutputThread out;// 传递进来的写消息线程，因为我们要给用户回复消息啊
	private OutputThreadMap map;// 写消息线程缓存器
	private ObjectInputStream ois;// 对象输入流
	private boolean isStart = true;// 是否循环读消息

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		try {
			ois = new ObjectInputStream(socket.getInputStream());// 实例化对象输入流
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStart(boolean isStart) {// 提供接口给外部关闭读消息线程
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// 读取消息
				readMessage();
			}
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void getOffLineMessage(UserDao dao,int fromU,int toU)
	{
		//Send the offline message 
		ArrayList<TranObject<TextMessage>> list_off_line_mss = dao.getOffLineMessage( fromU, toU);
		System.out.println("8888888888888   "+list_off_line_mss.size());
		if (list_off_line_mss != null) {// 如果登录成功					
			for (TranObject<TextMessage> ms : list_off_line_mss) {
				System.out.println("77777   "+list_off_line_mss.size());
				out.setMessage(ms);// 
			}
		}
	}
	public void getCrowdOffLineMessage(UserDao dao,int fromU,int toU,OutputThread out_t)
	{
		//Send the offline message 
		ArrayList<TranObject<TextMessage>> list_off_line_mss = dao.getCrowdOffLineMessage( fromU, toU);
		if (list_off_line_mss != null) {// 如果登录成功					
			for (TranObject<TextMessage> ms : list_off_line_mss) {
				out_t.setMessage(ms);// 
			}
		}
	}
	/**
	 * 读消息以及处理消息，抛出异常
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		Object readObject = ois.readObject();// 从流中读取对象
		UserDao dao = UserDaoFactory.getInstance();// 通过dao模式管理后台
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;// 转换成传输对象
			switch (read_tranObject.getType()) {
			case REGISTER:// 如果用户是注册
				User registerUser = (User) read_tranObject.getObject();
				int registerResult = dao.register(registerUser);
				System.out.println(MyDate.getDateCN() + " 新用户注册:"
						+ registerResult);
				// 给用户回复消息
				TranObject<User> register2TranObject = new TranObject<User>(
						TranObjectType.REGISTER);
				User register2user = new User();
				register2user.setId(registerResult);
				register2TranObject.setObject(register2user);
				out.setMessage(register2TranObject);
				break;
			case LOGIN:
				User loginUser = (User) read_tranObject.getObject();
				ArrayList<User> list = dao.login(loginUser);
				list.get(0).setOffLineMessUser(dao.haveOffLineMess(loginUser));//the first user in list should self.
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				if (list != null) {// 如果登录成功
					TranObject<User> onObject = new TranObject<User>(
							TranObjectType.LOGIN);
					User login2User = new User();
					login2User.setId(loginUser.getId());
					onObject.setObject(login2User);
					for (OutputThread onOut : map.getAll()) {
						onOut.setMessage(onObject);// 广播一下用户上线
					}
					map.add(loginUser.getId(), out);// 先广播，再把对应用户id的写线程存入map中，以便转发消息时调用
					login2Object.setObject(list);// 把好友列表加入回复的对象中
				} else {
					login2Object.setObject(null);
				}
				out.setMessage(login2Object);// 同时把登录信息回复给用户

				System.out.println(MyDate.getDateCN() + " 用户："
						+ loginUser.getId() + " 上线了");	
				break;
			case OFFLINEMESS:
				int toUs=0,fromUs=0;
				CommonMsg cm = (CommonMsg) read_tranObject.getObject();
				System.out.println(cm.getarg3());
				if(cm.getarg3().equals("0"))
					getOffLineMessage(dao,Integer.parseInt(cm.getarg2()),Integer.parseInt(cm.getarg1()));
				else
					getCrowdOffLineMessage(dao,Integer.parseInt(cm.getarg2()),Integer.parseInt(cm.getarg1()),out);
				
				break;
			case LOGOUT:// 如果是退出，更新数据库在线状态，同时群发告诉所有在线用户
				User logoutUser = (User) read_tranObject.getObject();
				int offId = logoutUser.getId();
				System.out
						.println(MyDate.getDateCN() + " 用户：" + offId + " 下线了");
				dao.logout(offId);
				isStart = false;// 结束自己的读循环
				map.remove(offId);// 从缓存的线程中移除
				out.setMessage(null);// 先要设置一个空消息去唤醒写线程
				out.setStart(false);// 再结束写线程循环

				TranObject<User> offObject = new TranObject<User>(
						TranObjectType.LOGOUT);
				User logout2User = new User();
				logout2User.setId(logoutUser.getId());
				offObject.setObject(logout2User);
				for (OutputThread offOut : map.getAll()) {// 广播用户下线消息
					offOut.setMessage(offObject);
				}
				break;
			case MESSAGE:// 如果是转发消息（可添加群发）
				// 获取消息中要转发的对象id，然后获取缓存的该对象的写线程
				int id2 = read_tranObject.getToUser();
				OutputThread toOut = map.getById(id2);
				if (toOut != null) {// 如果用户在线
					toOut.setMessage(read_tranObject);
				} else {// 如果为空，说明用户已经下线,回复用户
					int fromU = read_tranObject.getFromUser();
					TextMessage tm=(TextMessage)read_tranObject.getObject();
					if(read_tranObject.getCrowd()==0){
						TextMessage text = new TextMessage();
						text.setMessage("亲！对方不在线哦，您的消息将暂时保存在服务器");
						TranObject<TextMessage> offText = new TranObject<TextMessage>(
								TranObjectType.MESSAGE);
						offText.setObject(text);
						offText.setFromUser(0);
						out.setMessage(offText);
						//we should save the message on db
						dao.saveMessageOnDB(tm,fromU,id2);
					}
					else if(read_tranObject.getCrowd()==id2)
					{
						dao.saveCrowdMessageOnDB(tm,fromU,id2);
						for (Map.Entry<Integer, OutputThread> entry : map.getMap().entrySet()) {
							if(entry.getKey()!=fromU)
								getCrowdOffLineMessage(dao,entry.getKey(),id2,entry.getValue());
							
						}
						
					}	
				}
				break;
			case REFRESH:
				List<User> refreshList = dao.refresh(read_tranObject
						.getFromUser());
				TranObject<List<User>> refreshO = new TranObject<List<User>>(
						TranObjectType.REFRESH);
				refreshO.setObject(refreshList);
				out.setMessage(refreshO);
				System.out.println(MyDate.getDateCN() + " 刷新朋友状态");
				break;
			case ALLUSERS:
				User loginUser1 = (User) read_tranObject.getObject();
				ArrayList<User> list1 = dao.allUsers(loginUser1);
				TranObject<ArrayList<User>> login2Object1 = new TranObject<ArrayList<User>>(
						TranObjectType.ALLUSERS);
				if (list1 != null) {
					login2Object1.setObject(list1);// 把好友列表加入回复的对象中
				} else {
					login2Object1.setObject(null);
				}
				out.setMessage(login2Object1);// 同时把登录信息回复给用户

				System.out.println(MyDate.getDateCN() + " 显示所有用户信息");
				break;
			case ADDFRIEND:
				AddNewFriendMsg friends = (AddNewFriendMsg) read_tranObject.getObject();
				TextMessage text = new TextMessage();
				TranObject<TextMessage> infoText;
				if( dao.addFriends(friends) )
				{	
					text.setMessage("添加新朋友成功,你马上可以和他聊天料哦!");		
					infoText = new TranObject<TextMessage>(
							TranObjectType.ISOK);
					System.out.println(MyDate.getDateCN() + " 添加新朋友成功!");
				}else
				{
					text.setMessage("添加新朋友失败,请联系管理员!");	
					infoText = new TranObject<TextMessage>(
							TranObjectType.ISERROR);
					System.out.println(MyDate.getDateCN() + " 添加新朋友失败!");
				}				
				
				infoText.setObject(text);
				infoText.setFromUser(0);
				out.setMessage(infoText);
				
				break;
				
			default:
				break;
			}
		}
	}
}

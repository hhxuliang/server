package com.way.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.CommonMsg;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.chat.common.util.MyDate;
import com.way.chat.dao.UserDao;
import com.way.chat.dao.impl.UserDaoFactory;

/**
 * ����Ϣ�̺߳ʹ�����
 * 
 * @author way
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket����
	private OutputThread out;// ���ݽ�����д��Ϣ�̣߳���Ϊ����Ҫ���û��ظ���Ϣ��
	private OutputThreadMap map;// д��Ϣ�̻߳�����
	private MyInputStream ois;// ����������
	private boolean isStart = true;// �Ƿ�ѭ������Ϣ
	private int key = 0;
	private String keystr;

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map,
			String strs) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		keystr = strs;
		try {
			if(socket.getLocalPort()==Constants.SERVER_PORT_IOS)
				ois = new MyInputStream(socket.getInputStream(), 0);// ʵ��������������
			else if(socket.getLocalPort()==Constants.SERVER_PORT)
				ois = new MyInputStream(socket.getInputStream(), 1);// ʵ��������������
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStart(boolean isStart) {// �ṩ�ӿڸ��ⲿ�رն���Ϣ�߳�
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// ��ȡ��Ϣ
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
		System.out.println("socket discon.............");
		try {
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (map != null && map.getById(key) != null
				&& map.getById(key).getKeystr() != null
				&& map.getById(key).getKeystr().equals(keystr))
			map.remove(key);
	}

	public void getOffLineMessage(UserDao dao, int fromU) {
		// Send the offline message
		ArrayList<TranObject<TextMessage>> list_off_line_mss = dao
				.getOffLineMessage(fromU);
		if (list_off_line_mss != null) {// �����¼�ɹ�
			for (TranObject<TextMessage> ms : list_off_line_mss) {
				out.setMessage(ms);//
			}
		}
	}

	public void getCrowdOffLineMessage(UserDao dao, int fromU, int crowd,
			String where) {
		// Send the offline message
		ArrayList<TranObject<TextMessage>> list_off_line_mss = dao
				.getCrowdOffLineMessage(fromU, crowd, where);
		if (list_off_line_mss != null) {// �����¼�ɹ�
			for (TranObject<TextMessage> ms : list_off_line_mss) {
				out.setMessage(ms);//
			}
		}
	}

	/**
	 * ����Ϣ�Լ�������Ϣ���׳��쳣
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		Object readObject = ois.readObject();// �����ж�ȡ����
		UserDao dao = UserDaoFactory.getInstance();// ͨ��daoģʽ�����̨
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;// ת���ɴ������
			switch (read_tranObject.getType()) {
			case REGISTER:// ����û���ע��
				User registerUser = (User) read_tranObject.getObject();
				int registerResult = dao.register(registerUser);
				System.out
						.println(MyDate.getDateCN() + " 注册:" + registerResult);
				// ���û��ظ���Ϣ
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
				if (list == null)
					break;
				list.get(0).setOffLineMessUser(dao.haveOffLineMess(loginUser));// the
																				// first
																				// user
																				// in
																				// list
																				// should
																				// self.
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);

				map.add(loginUser.getId(), out);
				login2Object.setObject(list);// �Ѻ����б����ظ��Ķ�����
				key = loginUser.getId();

				out.setMessage(login2Object);// ͬʱ�ѵ�¼��Ϣ�ظ����û�

				System.out.println(MyDate.getDateCN() + "用户"
						+ loginUser.getId() + " 登陆");
				getOffLineMessage(dao, key);
				// getCrowdOffLineMessage(dao, Integer.parseInt(cm.getarg2()),
				// Integer.parseInt(cm.getarg1()), out);

				break;
			case LOGOUT:// ������˳����������ݿ�����״̬��ͬʱȺ���������������û�
				User logoutUser = (User) read_tranObject.getObject();
				int offId = logoutUser.getId();
				System.out.println(MyDate.getDateCN() + " �û���" + offId
						+ " ������");
				dao.logout(offId);
				isStart = false;// �����Լ��Ķ�ѭ��
				map.remove(offId);// �ӻ�����߳����Ƴ�
				out.setMessage(null);// ��Ҫ����һ������Ϣȥ����д�߳�
				out.setStart(false);// �ٽ���д�߳�ѭ��

				TranObject<User> offObject = new TranObject<User>(
						TranObjectType.LOGOUT);
				User logout2User = new User();
				logout2User.setId(logoutUser.getId());
				offObject.setObject(logout2User);
				for (OutputThread offOut : map.getAll()) {// �㲥�û�������Ϣ
					offOut.setMessage(offObject);
				}
				break;
			case MESSAGE:// �����ת����Ϣ�������Ⱥ����
				// ��ȡ��Ϣ��Ҫת���Ķ���id��Ȼ���ȡ����ĸö����д�߳�
				int id2 = read_tranObject.getToUser();
				int fromU = read_tranObject.getFromUser();
				OutputThread toOut = map.getById(id2);
				TextMessage tm = (TextMessage) read_tranObject.getObject();
				String datestr = MyDate.getDateMillis();

				CommonMsg cmg = new CommonMsg();
				cmg.setarg1(tm.getDatekey());
				cmg.setarg2(id2 + "");
				TranObject<CommonMsg> ack = new TranObject<CommonMsg>(
						TranObjectType.ACKMSG);
				ack.setObject(cmg);
				ack.setFromUser(0);
				out.setMessage(ack);
				if (false == dao.addMsg(tm, id2, fromU, datestr))
					break;
				System.out.println("crowd id is " + read_tranObject.getCrowd());

				((TextMessage) read_tranObject.getObject())
						.setServerdatekey(datestr);
				if (toOut != null && read_tranObject.getCrowd() == 0) {// ����û�����
					toOut.setMessage(read_tranObject);
					System.out.println("get message:" + tm.getMessage());
				} else {// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
					if (read_tranObject.getCrowd() > 0) {
						System.out.println("crowd id is "
								+ read_tranObject.getCrowd());
						ArrayList<String> onlineid = null;
						onlineid = dao.getFriends(read_tranObject.getCrowd());
						if (onlineid != null && onlineid.size() > 0) {
							for (String ss : onlineid) {
								if (!ss.equals(read_tranObject.getFromUser()
										+ "")) {
									OutputThread toOutt = map.getById(Integer
											.parseInt(ss));
									if (toOutt != null) {
										read_tranObject.setToUser(Integer
												.parseInt(ss));
										// read_tranObject.setCrowd(id);
										toOutt.setMessage(read_tranObject);
										System.out.println("is online :" + ss);
									}
								}
							}
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
				System.out.println(MyDate.getDateCN() + " ˢ������״̬");
				break;
			case ALLUSERS:
				User loginUser1 = (User) read_tranObject.getObject();
				ArrayList<User> list1 = dao.allUsers(loginUser1);
				TranObject<ArrayList<User>> login2Object1 = new TranObject<ArrayList<User>>(
						TranObjectType.ALLUSERS);
				if (list1 != null) {
					login2Object1.setObject(list1);// �Ѻ����б����ظ��Ķ�����
				} else {
					login2Object1.setObject(null);
				}
				out.setMessage(login2Object1);// ͬʱ�ѵ�¼��Ϣ�ظ����û�

				System.out.println(MyDate.getDateCN() + " ��ʾ�����û���Ϣ");
				break;
			case ADDFRIEND:
				AddNewFriendMsg friends = (AddNewFriendMsg) read_tranObject
						.getObject();
				TextMessage text = new TextMessage();
				TranObject<TextMessage> infoText;
				if (dao.addFriends(friends)) {
					text.setMessage("��������ѳɹ�,�����Ͽ��Ժ���������Ŷ!");
					infoText = new TranObject<TextMessage>(TranObjectType.ISOK);
					System.out.println(MyDate.getDateCN() + " ��������ѳɹ�!");
				} else {
					text.setMessage("���������ʧ��,����ϵ����Ա!");
					infoText = new TranObject<TextMessage>(
							TranObjectType.ISERROR);
					System.out.println(MyDate.getDateCN() + " ���������ʧ��!");
				}

				infoText.setObject(text);
				infoText.setFromUser(0);
				out.setMessage(infoText);

				break;
			case HEARTBEAT:
				CommonMsg com = new CommonMsg();
				com.setarg1("1");
				com.setarg2("1");
				com.setarg3("1");
				TranObject<CommonMsg> msg2Object = new TranObject<CommonMsg>(
						TranObjectType.HEARTBEAT);
				msg2Object.setObject(com);
				out.setMessage(msg2Object);
			case ACKMSG:
				CommonMsg cm = (CommonMsg) read_tranObject.getObject();
				int fromUid = read_tranObject.getFromUser();

				if (cm.getarg1() != null && cm.getarg1().length() > 0)
					dao.updateDBbyMsgOk(cm.getarg1(), fromUid);
				break;
			case VERSION:
				CommonMsg cm_v = (CommonMsg) read_tranObject.getObject();
				fromUid = read_tranObject.getFromUser();

				if (cm_v.getarg1() != null && cm_v.getarg1().length() > 0)
					if (cm_v.getarg1().compareTo(Constants.VERSION) != 0) {
						CommonMsg com_v = new CommonMsg();
						com_v.setarg1(Constants.VERSION);
						TranObject<CommonMsg> msg2Object_v = new TranObject<CommonMsg>(
								TranObjectType.VERSION);
						msg2Object_v.setObject(com_v);
						out.setMessage(msg2Object_v);
					}
				break;
			case CROWDOFFLINEMSG:
				CommonMsg cmm = (CommonMsg) read_tranObject.getObject();
				String where = "";
				int idforu = read_tranObject.getFromUser();
				int crowdid = read_tranObject.getCrowd();
				System.out.println("get crowd off line msg");
				if (cmm.getarg1() != null && cmm.getarg1().length() > 0) {
					where = cmm.getarg1();

				}
				getCrowdOffLineMessage(dao, idforu, crowdid, where);
				break;
			default:
				break;
			}
		}
	}
}

class MyInputStream {

	private InputStreamReader isr = null;
	BufferedReader br = null;
	private ObjectInputStream ois = null;
	int streamType = -1;

	public MyInputStream(InputStream o, int stream_type) throws IOException {
		streamType = stream_type;
		switch (streamType) {
		case 0:
			isr = new InputStreamReader(o,"UTF-8");
			br = new BufferedReader(isr);
			break;
		case 1:
			ois = new ObjectInputStream(o);
			break;
		default:
			break;
		}

	}

	public Object readObject() throws IOException, ClassNotFoundException {
		switch (streamType) {
		case 0:
			
			String read = new String();
			TranObject obj = null;
 			read = br.readLine();
			if (read != null && read.length() > 0) {
				System.out.println(read);
				JSONObject jsonObj = JSONObject.fromObject(read);
				int name = jsonObj.getInt("TranObjectType");
				switch (name) {
				case 0:
					break;
				case 1:
					obj = new TranObject<User>(TranObjectType.LOGIN);
					obj.setToUser(jsonObj.getInt("toUser"));
					JSONObject jsonObj_u = jsonObj.getJSONObject("TranObject");
					User u = new User();
					u.setName(jsonObj_u.getString("name"));
					u.setLoginAccount(jsonObj_u.getString("name"));
					u.setPassword(jsonObj_u.getString("password"));
					obj.setObject(u);
					break;
				case 5:
					obj = new TranObject<User>(TranObjectType.MESSAGE);
					obj.setToUser(jsonObj.getInt("toUser"));
					obj.setFromUser(jsonObj.getInt("fromUser"));
					JSONObject jsonObj_msg = jsonObj.getJSONObject("TranObject");
					TextMessage msg = new TextMessage();
					msg.setMessage(jsonObj_msg.getString("content"));
					msg.setmsgtype(jsonObj_msg.getInt("type"));
					msg.setDatekey(jsonObj_msg.getString("timeSend"));
					obj.setObject(msg);
					break;
				case 16:
					obj = new TranObject<User>(TranObjectType.ACKMSG);
					obj.setToUser(jsonObj.getInt("toUser"));
					obj.setFromUser(jsonObj.getInt("fromUser"));
					JSONObject jsonObj_ack = jsonObj.getJSONObject("TranObject");
					CommonMsg cmsg = new CommonMsg();
					cmsg.setarg1(jsonObj_ack.getString("arg1"));
					cmsg.setarg2(jsonObj_ack.getString("arg2"));
					cmsg.setarg3(jsonObj_ack.getString("arg3"));
					obj.setObject(cmsg);
					break;
				}
			}
			return obj;
		case 1:
			return ois.readObject();
		default:
			break;
		}
		return null;
	}

	public void close() throws IOException {
		switch (streamType) {
		case 0:
			isr.close();
			break;
		case 1:
			ois.close();
			break;
		default:
			break;
		}
	}
}
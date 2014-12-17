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
 * ����Ϣ�̺߳ʹ�����
 * 
 * @author way
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket����
	private OutputThread out;// ���ݽ�����д��Ϣ�̣߳���Ϊ����Ҫ���û��ظ���Ϣ��
	private OutputThreadMap map;// д��Ϣ�̻߳�����
	private ObjectInputStream ois;// ����������
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
			ois = new ObjectInputStream(socket.getInputStream());// ʵ��������������
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
		if (map.getById(key).getKeystr().equals(keystr))
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
				dao.addMsg(tm, id2, fromU, datestr);
				CommonMsg cmg = new CommonMsg();
				cmg.setarg1(tm.getDatekey());
				cmg.setarg2(id2 + "");
				TranObject<CommonMsg> ack = new TranObject<CommonMsg>(
						TranObjectType.ACKMSG);
				ack.setObject(cmg);
				ack.setFromUser(0);
				out.setMessage(ack);
				((TextMessage) read_tranObject.getObject())
						.setServerdatekey(datestr);
				if (toOut != null) {// ����û�����
					toOut.setMessage(read_tranObject);
					System.out.println("get message:" + tm.getMessage());
				} else {// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
					if (read_tranObject.getCrowd() > 0) {
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

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

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
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

	}

	public void getOffLineMessage(UserDao dao,int fromU,int toU)
	{
		//Send the offline message 
		ArrayList<TranObject<TextMessage>> list_off_line_mss = dao.getOffLineMessage( fromU, toU);
		System.out.println("8888888888888   "+list_off_line_mss.size());
		if (list_off_line_mss != null) {// �����¼�ɹ�					
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
		if (list_off_line_mss != null) {// �����¼�ɹ�					
			for (TranObject<TextMessage> ms : list_off_line_mss) {
				out_t.setMessage(ms);// 
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
				System.out.println(MyDate.getDateCN() + " ���û�ע��:"
						+ registerResult);
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
				list.get(0).setOffLineMessUser(dao.haveOffLineMess(loginUser));//the first user in list should self.
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				if (list != null) {// �����¼�ɹ�
					TranObject<User> onObject = new TranObject<User>(
							TranObjectType.LOGIN);
					User login2User = new User();
					login2User.setId(loginUser.getId());
					onObject.setObject(login2User);
					for (OutputThread onOut : map.getAll()) {
						onOut.setMessage(onObject);// �㲥һ���û�����
					}
					map.add(loginUser.getId(), out);// �ȹ㲥���ٰѶ�Ӧ�û�id��д�̴߳���map�У��Ա�ת����Ϣʱ����
					login2Object.setObject(list);// �Ѻ����б����ظ��Ķ�����
				} else {
					login2Object.setObject(null);
				}
				out.setMessage(login2Object);// ͬʱ�ѵ�¼��Ϣ�ظ����û�

				System.out.println(MyDate.getDateCN() + " �û���"
						+ loginUser.getId() + " ������");	
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
			case LOGOUT:// ������˳����������ݿ�����״̬��ͬʱȺ���������������û�
				User logoutUser = (User) read_tranObject.getObject();
				int offId = logoutUser.getId();
				System.out
						.println(MyDate.getDateCN() + " �û���" + offId + " ������");
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
				OutputThread toOut = map.getById(id2);
				if (toOut != null) {// ����û�����
					toOut.setMessage(read_tranObject);
				} else {// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
					int fromU = read_tranObject.getFromUser();
					TextMessage tm=(TextMessage)read_tranObject.getObject();
					if(read_tranObject.getCrowd()==0){
						TextMessage text = new TextMessage();
						text.setMessage("�ף��Է�������Ŷ��������Ϣ����ʱ�����ڷ�����");
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
				AddNewFriendMsg friends = (AddNewFriendMsg) read_tranObject.getObject();
				TextMessage text = new TextMessage();
				TranObject<TextMessage> infoText;
				if( dao.addFriends(friends) )
				{	
					text.setMessage("��������ѳɹ�,�����Ͽ��Ժ���������Ŷ!");		
					infoText = new TranObject<TextMessage>(
							TranObjectType.ISOK);
					System.out.println(MyDate.getDateCN() + " ��������ѳɹ�!");
				}else
				{
					text.setMessage("���������ʧ��,����ϵ����Ա!");	
					infoText = new TranObject<TextMessage>(
							TranObjectType.ISERROR);
					System.out.println(MyDate.getDateCN() + " ���������ʧ��!");
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

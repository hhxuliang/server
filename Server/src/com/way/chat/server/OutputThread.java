package com.way.chat.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.way.chat.common.tran.bean.TranObject;

/**
 * д��Ϣ�߳�
 * 
 * @author way
 * 
 */
public class OutputThread extends Thread {
	private OutputThreadMap map;
	private ObjectOutputStream oos;
	private ArrayList<TranObject> object = new ArrayList<TranObject>();
	private boolean isStart = true;// ѭ����־λ
	private Socket socket;

	public OutputThread(Socket socket, OutputThreadMap map) {
		try {
			this.socket = socket;
			this.map = map;
			oos = new ObjectOutputStream(socket.getOutputStream());// �ڹ���������ʵ�������������
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	// ����д��Ϣ�̣߳���������Ϣ֮�󣬻���run���������Խ�Լ��Դ
	public void setMessage(TranObject o) {
		synchronized (this) {
			this.object.add(o);
			notify();
		}
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// û����Ϣд����ʱ���̵߳ȴ�
				synchronized (this) {
					wait();
				
					if (object != null) {
						for(TranObject o:object)
						{
							oos.writeObject(o);
							oos.flush();
						}
						object.clear();
					}
				}
			}
			if (oos != null)// ѭ�������󣬹ر������ͷ���Դ
				oos.close();
			if (socket != null)
				socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

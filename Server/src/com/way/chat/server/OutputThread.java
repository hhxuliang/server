package com.way.chat.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.way.chat.common.tran.bean.TranObject;

/**
 * д��Ϣ�߳�
 * 
 * @author way
 * 
 */
public class OutputThread extends Thread {
	private OutputThreadMap map;
	private MyOutputStream oos;
	private List<TranObject> object = Collections
			.synchronizedList(new ArrayList<TranObject>());
	private boolean isStart = true;// ѭ����־λ
	private Socket socket;
	private String keystr;

	public String getKeystr() {
		return keystr;
	}

	public void setKeystr(String keystr) {
		this.keystr = keystr;
	}

	public OutputThread(Socket socket, OutputThreadMap map, String keys) {
		try {
			keystr = keys;
			this.socket = socket;
			this.map = map;
			oos = new MyOutputStream(socket.getOutputStream(),1);// �ڹ���������ʵ�������������
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
					Iterator i = object.iterator(); // Must be in synchronized
													// block
					while (i.hasNext()) {
						TranObject o = (TranObject) i.next();
						oos.writeObject(o);
						oos.flush();
						oos.reset();
					}
					object.clear();

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

class MyOutputStream {

	private PrintWriter pw = null;
	private ObjectOutputStream oos = null;
	int streamType = -1;

	public MyOutputStream(OutputStream o, int stream_type) throws IOException {
		streamType = stream_type;
		switch (streamType) {
		case 0:
			pw = new PrintWriter(o);
			break;
		case 1:
			oos = new ObjectOutputStream(o);
			break;
		default:
			break;
		}

	}

	public void writeObject(TranObject o) throws IOException {
		switch (streamType) {
		case 0:
			//pw.println(o.toString());
			break;
		case 1:
			oos.writeObject(o);
			break;
		default:
			break;
		}

	}

	public void flush() throws IOException {
		switch (streamType) {
		case 0:
			pw.flush();
			break;
		case 1:
			oos.flush();
			break;
		default:
			break;
		}
	}

	public void reset() throws IOException {
		switch (streamType) {
		case 0:
			//pw.reset();
			break;
		case 1:
			oos.reset();
			break;
		default:
			break;
		}
	}

	public void close() throws IOException {
		switch (streamType) {
		case 0:
			pw.close();
			break;
		case 1:
			oos.close();
			break;
		default:
			break;
		}
	}
}

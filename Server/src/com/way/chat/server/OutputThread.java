package com.way.chat.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.way.chat.common.tran.bean.TranObject;

/**
 * 写消息线程
 * 
 * @author way
 * 
 */
public class OutputThread extends Thread {
	private OutputThreadMap map;
	private ObjectOutputStream oos;
	private ArrayList<TranObject> object = new ArrayList<TranObject>();
	private boolean isStart = true;// 循环标志位
	private Socket socket;

	public OutputThread(Socket socket, OutputThreadMap map) {
		try {
			this.socket = socket;
			this.map = map;
			oos = new ObjectOutputStream(socket.getOutputStream());// 在构造器里面实例化对象输出流
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	// 调用写消息线程，设置了消息之后，唤醒run方法，可以节约资源
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
				// 没有消息写出的时候，线程等待
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
			if (oos != null)// 循环结束后，关闭流，释放资源
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

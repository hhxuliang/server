package com.way.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.way.chat.common.util.Constants;
import com.way.chat.common.util.MyDate;

/**
 * �������������û���¼�����ߡ�ת����Ϣ
 * 
 * @author way
 * 
 */
public class Server extends Thread
{
	private ExecutorService executorService;// �̳߳�
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private boolean isStarted = true;

	public Server()
	{
		try
		{
			// �����̳߳أ����о���(cpu����*50)���߳�
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 50);
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			quit();
		}
	}

	@Override
	public void run()
	{
		try
		{
			while (isStarted)
			{
				socket = serverSocket.accept();
				String ip = socket.getInetAddress().toString();
				System.out.println(MyDate.getDateCN() + " �û���" + ip + " �ѽ�������");
				// Ϊ֧�ֶ��û��������ʣ������̳߳ع���ÿһ���û�����������
				if (socket.isConnected())
					executorService.execute(new SocketTask(socket));// ��ӵ��̳߳�
			}
			if (socket != null)
				socket.close();
			if (serverSocket != null)
				serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			// isStarted = false;
		}
	}
	public static String getDateMillis() {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy_MM_dd_hh_mm_ss_SSS");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
	}
	private final class SocketTask implements Runnable
	{
		private Socket socket = null;
		private InputThread in;
		private OutputThread out;
		private OutputThreadMap map;

		public SocketTask(Socket socket)
		{
			this.socket = socket;
			map = OutputThreadMap.getInstance();
		}

		@Override
		public void run()
		{
			String str=getDateMillis();
			out = new OutputThread(socket, map,str);//
			// ��ʵ����д��Ϣ�߳�,���Ѷ�Ӧ�û���д�̴߳���map�������У�
			in = new InputThread(socket, out, map,str);// ��ʵ��������Ϣ�߳�
			out.setStart(true);
			in.setStart(true);
			in.start();
			out.start();
		}
	}

	/**
	 * �˳�
	 */
	public void quit()
	{
		try
		{
			this.isStarted = false;
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new Server().start();
	}
}

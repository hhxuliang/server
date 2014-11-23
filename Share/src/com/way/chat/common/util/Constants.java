package com.way.chat.common.util;

public class Constants
{
	public static final String SERVER_IP = "192.168.1.103";// 服务器IP
	public static final int SERVER_PORT = 8000;// 服务器端口

	public static final int CLIENT_SERVER_PORT = 8001;// 客户端服务器端口
	public static final int CLIENT_FILE_TRANSPORT_PORT = 8002;// 客户端接收文件端口
	public static final int REGISTER_FAIL_EMAIL = -1;
	public static final int REGISTER_FAIL_NAME = -2;

	public static final int REGISTER_FAIL = 0;//注册失败
	public static final String ACTION = "com.way.message";//消息广播action
	public static final String MSGKEY = "message";//消息的key
	public static final String IP_PORT = "ipPort";//保存ip、port的xml文件名
	public static final String SAVE_USER = "saveUser";//保存用户信息的xml文件名
	public static final String BACKKEY_ACTION = "com.way.backKey";//返回键发送广播的action
	public static final int NOTIFY_ID = 0x911;//通知ID
	public static final String DBNAME = "teather.db";//数据库名称

	public static final String FILE_UPLOAD_URL = "http://" + SERVER_IP + ":8080/Server/UploadFile";
}

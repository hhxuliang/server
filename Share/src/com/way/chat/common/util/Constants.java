package com.way.chat.common.util;

public class Constants
{
	public static final String SERVER_IP = "192.168.1.103";// ������IP
	public static final int SERVER_PORT = 8000;// �������˿�

	public static final int CLIENT_SERVER_PORT = 8001;// �ͻ��˷������˿�
	public static final int CLIENT_FILE_TRANSPORT_PORT = 8002;// �ͻ��˽����ļ��˿�
	public static final int REGISTER_FAIL_EMAIL = -1;
	public static final int REGISTER_FAIL_NAME = -2;

	public static final int REGISTER_FAIL = 0;//ע��ʧ��
	public static final String ACTION = "com.way.message";//��Ϣ�㲥action
	public static final String MSGKEY = "message";//��Ϣ��key
	public static final String IP_PORT = "ipPort";//����ip��port��xml�ļ���
	public static final String SAVE_USER = "saveUser";//�����û���Ϣ��xml�ļ���
	public static final String BACKKEY_ACTION = "com.way.backKey";//���ؼ����͹㲥��action
	public static final int NOTIFY_ID = 0x911;//֪ͨID
	public static final String DBNAME = "teather.db";//���ݿ�����

	public static final String FILE_UPLOAD_URL = "http://" + SERVER_IP + ":8080/Server/UploadFile";
}

package com.way.chat.common.tran.bean;

/**
 * �����������
 * 
 * @author way
 * 
 */
public enum TranObjectType {
	REGISTER, // ע��
	LOGIN, // �û���¼
	LOGOUT, // �û��˳���¼
	FRIENDLOGIN, // ��������
	FRIENDLOGOUT, // ��������
	MESSAGE, // �û�������Ϣ
	UNCONNECTED, // �޷�����
	FILE, // �����ļ�
	REFRESH,//ˢ�º����б�
	ADDFRIENDS,//��Ӻ��ѳɹ�
	ALLUSERS,//�����û���Ϣ
	ADDFRIEND,//�������
	ISOK, // �ɹ�
	ISERROR, // ʧ��
	OFFLINEMESS,//������Ϣ
	HEARTBEAT,
	ACKMSG,
	CROWDOFFLINEMSG,
	VERSION,
}

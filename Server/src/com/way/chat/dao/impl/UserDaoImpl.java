package com.way.chat.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.chat.common.util.DButil;
import com.way.chat.common.util.MyDate;
import com.way.chat.dao.UserDao;

public class UserDaoImpl implements UserDao {

	@Override
	public int register(User u) {
		int id;
		Connection con = DButil.connect();
		String sql1 = "insert into user(_name,_password,_email,_time,iscrowd) values(?,?,?,?,0)";
		String sql2 = "select _id from user";
		String sql3 = "select * from  user where _email=?";
		String sql4 = "select * from  user where _name=?";

		try {
			PreparedStatement ps3 = con.prepareStatement(sql3);
			ps3.setString(1, u.getEmail());
			ResultSet rs3 = ps3.executeQuery();
			if (!rs3.last()) {
				PreparedStatement ps4 = con.prepareStatement(sql4);
				ps4.setString(1, u.getName());
				ResultSet rs4 = ps4.executeQuery();
				if (!rs4.last()) {
					PreparedStatement ps = con.prepareStatement(sql1);
					ps.setString(1, u.getName());
					ps.setString(2, u.getPassword());
					ps.setString(3, u.getEmail());
					ps.setString(4, MyDate.getDateCN());
					int res = ps.executeUpdate();
					if (res > 0) {
						PreparedStatement ps2 = con.prepareStatement(sql2);
						ResultSet rs = ps2.executeQuery();
						if (rs.last()) {
							id = rs.getInt("_id");
							createFriendtable(id);// ע��ɹ��󣬴���һ�����û�idΪ�����ı����ڴ�ź�����Ϣ
							return id;
						}
					}
				} else {
					return Constants.REGISTER_FAIL_NAME;
				}
			} else {
				return Constants.REGISTER_FAIL_EMAIL;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return Constants.REGISTER_FAIL;
	}

	@Override
	public ArrayList<User> login(User u) {
		Connection con = DButil.connect();
		String sql = "select * from user where (_name=? or _email=?) and _password=?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, u.getLoginAccount());
			ps.setString(2, u.getLoginAccount());
			ps.setString(3, u.getPassword());
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				setOnline(rs.getInt("_id"));// ���±�״̬Ϊ����
				ArrayList<User> refreshList = refresh(rs.getInt("_id"));
				u.setId(rs.getInt("_id"));
				return refreshList;
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}
	@Override
	public ArrayList<String> getFriends(int uid) {
		Connection con = DButil.connect();
		ArrayList<String> listuid = new ArrayList<String>();
		String sql = "select * from _" + uid + "" ;
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do{
				listuid.add(rs.getInt("_qq")+"");
				}while (rs.next());
				return listuid;
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}
	@Override
	public ArrayList<User> allUsers(User u) {
		Connection con = DButil.connect();
		String sql = "select * from user where _id<>? and _id not in (select _qq from _?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, u.getId());
			ps.setInt(2, u.getId());
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				ArrayList<User> list = new ArrayList<User>();
				do {
					User friend = new User();
					friend.setId(rs.getInt("_id"));
					friend.setName(rs.getString("_name"));
					friend.setImg(rs.getInt("_img"));
					list.add(friend);
				} while (rs.next());
				return list;
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	@Override
	public boolean addFriends(AddNewFriendMsg a) {
		Connection con = DButil.connect();

		try {
			for (String s : a.getFriends()) {
				String sql = "insert into _"
						+ a.getUserID()
						+ " (_name,_isOnline,_group,_qq,_img) values ('',0,'�ҵĺ���',"
						+ s + ",0) ";
				System.out.println(sql);
				PreparedStatement ps = con.prepareStatement(sql);
				int res = ps.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return false;
	}

	@Override
	public boolean addMsg(TextMessage msg, int touid, int fromuid,
			String datestr) {
		Connection con = DButil.connect();

		try {
			String sql = "insert into _"
					+ touid
					+ "_msg (_msg,_fromuser,_msgtime,_type,_datekey,_readit) values ('"
					+ msg.getMessage() + "'," + fromuid + ",'" + datestr + "',"
					+ (msg.get_is_pic() ? 1 : 0) + ",'" + msg.getDatekey()
					+ "',0) ";
			System.out.println(sql);
			PreparedStatement ps = con.prepareStatement(sql);
			int res = ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return false;
	}

	public ArrayList<String> haveOffLineMess(User loginUser) {
		ArrayList<String> list = new ArrayList<String>();
		;
		Connection con = DButil.connect();
		String sql = "select distinct fromuserid from qq.offlinemessage where touserid="
				+ loginUser.getId()
				+ " union "
				+ "select crowdid from qq.crowdofflinemess where crowdid in("
				+ "select b._id from qq._"
				+ loginUser.getId()
				+ " as a, qq.user as b where a._qq=b._id and b.iscrowd=1) and readed=0 and userid="
				+ loginUser.getId();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					list.add(rs.getString("fromuserid"));
				} while (rs.next());
			}
			System.out.println(sql);
			return list;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return list;
	}

	/**
	 * �����Լ�
	 */
	public User findMe(int id) {
		User me = new User();
		Connection con = DButil.connect();
		String sql = "select * from user where _id=?";
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				me.setId(rs.getInt("_id"));
				me.setEmail(rs.getString("_email"));
				me.setName(rs.getString("_name"));
				me.setImg(rs.getInt("_img"));
				me.setGroup("我的好友");
				me.setIsCrowd(rs.getInt("iscrowd"));
			}
			return me;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	/**
	 * ˢ�º����б�
	 */
	public ArrayList<User> refresh(int id) {
		ArrayList<User> list = new ArrayList<User>();
		User me = findMe(id);
		list.add(me);// ������Լ�
		Connection con = DButil.connect();
		// String sql = "select * from _? ";
		String sql = "SELECT UP._id as _id,UP._group as _group,UP._qq as _qq,UP._img as _img,UP._isOnline as _isOnline,user._name as _name,user.iscrowd as iscrowd FROM UP,user where UP._qq=user._id ";
		sql = sql.replaceAll("UP", "_" + id);
		// System.out.println(sql);
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			// ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					User friend = new User();
					friend.setId(rs.getInt("_qq"));
					friend.setName(rs.getString("_name"));
					friend.setIsOnline(rs.getInt("_isOnline"));
					friend.setImg(rs.getInt("_img"));
					friend.setGroup(rs.getString("_group"));
					friend.setIsCrowd(rs.getInt("iscrowd"));
					System.out.println("iscrowd is " + friend.getIsCrowd());
					list.add(friend);
				} while (rs.next());
			}
			return list;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	/**
	 * ����״̬Ϊ����
	 * 
	 * @param id
	 */
	public void setOnline(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update user set _isOnline=1 where _id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
			updateAllOn(id);// �������б�״̬Ϊ����
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	/**
	 * ע��ɹ��󣬴���һ���û���������û�����
	 * 
	 * @param id
	 */
	public void createFriendtable(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "create table _" + id
					+ " (_id int auto_increment not null primary key,"
					+ "_name varchar(20) not null,"
					+ "_isOnline int(11) not null default 0,"
					+ "_group varchar(20) not null default 0,"
					+ "_qq int(11) not null default 0,"
					+ "_img int(11) not null default 0) DEFAULT CHARSET=utf8";
			PreparedStatement ps = con.prepareStatement(sql);
			int res = ps.executeUpdate();
			sql = "create table _" + id + "_msg"
					+ " (_id int auto_increment not null primary key,"
					+ "_msg varchar(255) not null,"
					+ "_fromuser int(11) not null default 0,"
					+ "_msgtime varchar(30),"
					+ "_type int(11) not null default 0,"
					+ "_datekey varchar(30) not null ," + "_readit int(11)) DEFAULT CHARSET=utf8";
			ps = con.prepareStatement(sql);
			res = ps.executeUpdate();
			System.out.println(res);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	@Override
	/**
	 * ���߸���״̬Ϊ����
	 */
	public void logout(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update user set _isOnline=0 where _id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
			updateAllOff(id);
			// System.out.println(res);
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	/**
	 * ���������û���״̬Ϊ����
	 * 
	 * @param id
	 */
	public void updateAllOff(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update _? set _isOnline=0 where _qq=?";
			PreparedStatement ps = con.prepareStatement(sql);
			for (int offId : getAllId()) {
				ps.setInt(1, offId);
				ps.setInt(2, id);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	/**
	 * ���������û�״̬Ϊ����
	 * 
	 * @param id
	 */
	public void updateAllOn(int id) {
		Connection con = DButil.connect();
		try {
			String sql = "update _? set _isOnline=1 where _qq=?";
			PreparedStatement ps = con.prepareStatement(sql);
			for (int OnId : getAllId()) {
				ps.setInt(1, OnId);
				ps.setInt(2, id);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	public boolean updateDBbyMsgOk(String datekey, int fromuid) {
		Connection con = DButil.connect();
		try {
			String sql = "update _" + fromuid
					+ "_msg set _readit=1 where _datekey='" + datekey + "'";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.executeUpdate();
			System.out.println(sql);
			return true;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return false;
	}

	public List<Integer> getAllId() {
		Connection con = DButil.connect();
		List<Integer> list = new ArrayList<Integer>();
		try {
			String sql = "select _id from user";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					int id = rs.getInt("_id");
					list.add(id);
				} while (rs.next());
			}
			// System.out.println(list);
			return list;
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	public boolean saveCrowdMessageOnDB(TextMessage tm, int fromU, int toU) {
		int msid = saveMessageOnDB(tm, fromU, toU);
		if (msid == -1)
			return false;
		User tU = findMe(toU);
		Connection con = DButil.connect();
		String sql = "";

		try {
			sql = "insert  crowdofflinemess (crowdid,userid,offlinemessid,readed) "
					+ "select userid_a,userid_b,"
					+ msid
					+ ",0 from friendship where userid_a = "
					+ toU
					+ " and userid_b <> "
					+ fromU
					+ " union "
					+ "select userid_b,userid_a,"
					+ msid
					+ ",0 from friendship where userid_b = "
					+ toU
					+ " and userid_a <> " + fromU;
			System.out.println(sql);
			PreparedStatement ps = con.prepareStatement(sql);
			int res = ps.executeUpdate();

			if (res > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return false;
	}

	public int saveMessageOnDB(TextMessage tm, int fromU, int toU) {
		User tU = findMe(toU);
		Connection con = DButil.connect();
		String sql = "";

		try {
			sql = "insert  offlinemessage(message,fromuserid,touserid) values ('"
					+ tm.getMessage() + "'," + fromU + "," + toU + ")";
			PreparedStatement ps = con.prepareStatement(sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			int res = ps.executeUpdate();
			if (res > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				int id = -1;
				if (rs.next()) {
					id = rs.getInt(1);
				}
				return id;
			} else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return -1;
	}

	public ArrayList<TranObject<TextMessage>> getOffLineMessage(int fromU) {
		Connection con = DButil.connect();
		ArrayList<TranObject<TextMessage>> list = new ArrayList<TranObject<TextMessage>>();
		try {
			// here get all normal message
			String sql = "select * from _" + fromU + "_msg a,user b where a._fromuser=b._id and a._readit=0";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					TextMessage tm = new TextMessage();
					tm.setMessage(rs.getString("_msg"));
					tm.set_is_pic((rs.getInt("_type") == 0 ? false : true));
					tm.setDatekey(rs.getString("_datekey"));
					tm.setServerdatekey(rs.getString("_msgtime"));
					tm.setMessageid(rs.getInt("_id"));
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(tm);
					offText.setFromUser(rs.getInt("_fromuser"));
					offText.setFromUserName(rs.getString("_name"));
					offText.setFromImg(rs.getInt("_img"));
					offText.setToUser(fromU);
					offText.setCrowd(-1);
					list.add(offText);
				} while (rs.next());
			}
			ps.close();
			System.out.println(sql);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}

	public ArrayList<TranObject<TextMessage>> getCrowdOffLineMessage(int uid,
			int crowdid, String where) {
		Connection con = DButil.connect();
		ArrayList<TranObject<TextMessage>> list = new ArrayList<TranObject<TextMessage>>();
		String sql="";
		try {
			// here get all normal message
			if (where == null || where.equals("")) {
				sql = "select * from _" + crowdid + "_msg a, user b"
						+ " where a._fromuser <> " + uid;

			} else {
				sql = "select * from _" + crowdid + "_msg a, user b"
						+ " where a._fromuser <> " + uid
						+ "  and a._fromuser=b._id and a._msgtime >'" + where + "'";
			}
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int index = 0;
			if (rs.first()) {
				do {
					TextMessage tm = new TextMessage();
					tm.setMessage(rs.getString("_msg"));
					tm.set_is_pic((rs.getInt("_type") == 0 ? false : true));
					tm.setDatekey(rs.getString("_datekey"));
					tm.setServerdatekey(rs.getString("_msgtime"));
					tm.setMessageid(rs.getInt("_id"));
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(tm);
					offText.setFromUser(rs.getInt("_fromuser"));
					offText.setFromUserName(rs.getString("_name"));
					offText.setFromImg(rs.getInt("_img"));
					offText.setToUser(uid);
					offText.setCrowd(crowdid);
					list.add(offText);
				} while (rs.next());
			}
			ps.close();
			System.out.println(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return list;

	}

	public static void main(String[] args) {
		User u = new User();
		UserDaoImpl dao = new UserDaoImpl();
		// u.setId(2016);
		// u.setName("qq");
		// u.setPassword("123");
		// u.setEmail("158342219@qq.com");
		// System.out.println(dao.register(u));
		// // System.out.println(dao.login(u));
		// // dao.logout(2016);
		// dao.setOnline(2016);
		// // dao.getAllId();
		List<User> list = dao.refresh(2016);
		System.out.println(list);

	}

}

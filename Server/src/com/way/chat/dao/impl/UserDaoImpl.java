package com.way.chat.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.way.chat.common.bean.AddNewFriendMsg;
import com.way.chat.common.bean.User;
import com.way.chat.common.util.Constants;
import com.way.chat.common.util.DButil;
import com.way.chat.common.util.MyDate;
import com.way.chat.dao.UserDao;

public class UserDaoImpl implements UserDao {

	@Override
	public int register(User u) {
		int id;
		Connection con = DButil.connect();
		String sql1 = "insert into user(_name,_password,_email,_time) values(?,?,?,?)";
		String sql2 = "select _id from user";
		String sql3 = "select * from  user where _email=?";
		String sql4 = "select * from  user where _name=?";
		

		try {
			PreparedStatement ps3 = con.prepareStatement(sql3);
			ps3.setString(1, u.getEmail());
			ResultSet rs3 = ps3.executeQuery();
			if (!rs3.last()) 
			{
				PreparedStatement ps4 = con.prepareStatement(sql4);
				ps4.setString(1, u.getName());
				ResultSet rs4 = ps4.executeQuery();
				if (!rs4.last()) 
				{
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
							createFriendtable(id);// 注册成功后，创建一个已用户id为表名的表，用于存放好友信息
							return id;
						}
					}
				}
				else
				{
					return Constants.REGISTER_FAIL_NAME;
				}
			}
			else
			{
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
				setOnline(rs.getInt("_id"));// 更新表状态为在线
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
	public boolean addFriends(AddNewFriendMsg a)
	{
		Connection con = DButil.connect();
		
		try {
			for(String s:a.getFriends())
			{
				String sql = "insert into _" + a.getUserID() + " (_name,_isOnline,_group,_qq,_img) values ('',0,0,"+s+",0) ";
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
	
	/**
	 * 查找自己
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
	 * 刷新好友列表
	 */
	public ArrayList<User> refresh(int id) {
		ArrayList<User> list = new ArrayList<User>();
		User me = findMe(id);
		list.add(me);// 先添加自己
		Connection con = DButil.connect();
		//String sql = "select * from _? ";
		String sql = "SELECT UP._id as _id,UP._group as _group,UP._qq as _qq,UP._img as _img,UP._isOnline as _isOnline,user._name as _name FROM UP,user where UP._qq=user._id ";
		sql=sql.replaceAll("UP", "_"+id);
		//System.out.println(sql);
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			//ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				do {
					User friend = new User();
					friend.setId(rs.getInt("_qq"));
					friend.setName(rs.getString("_name"));
					friend.setIsOnline(rs.getInt("_isOnline"));
					friend.setImg(rs.getInt("_img"));
					friend.setGroup(rs.getInt("_group"));
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
	 * 设置状态为在线
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
			updateAllOn(id);// 更新所有表状态为在线
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	/**
	 * 注册成功后，创建一个用户表，保存该用户好友
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
					+ "_group int(11) not null default 0,"
					+ "_qq int(11) not null default 0,"
					+ "_img int(11) not null default 0)";
			PreparedStatement ps = con.prepareStatement(sql);
			int res = ps.executeUpdate();
			System.out.println(res);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.close(con);
		}
	}

	@Override
	/**
	 * 下线更新状态为离线
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
	 * 更新所有用户表状态为离线
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
	 * 更新所有用户状态为上线
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

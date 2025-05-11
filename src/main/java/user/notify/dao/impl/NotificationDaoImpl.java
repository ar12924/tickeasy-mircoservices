package user.notify.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import user.notify.dao.NotificationDao;
import user.notify.vo.Notification;


public class NotificationDaoImpl implements NotificationDao {
	private DataSource ds;
	public NotificationDaoImpl() throws NamingException {
		ds =(DataSource) new InitialContext()
				.lookup("java:comp/env/jdbc/tickeasy");
	}

	@Override
	public List<Notification> selectAllByMemberId(int memberId) {
		List<Notification> notificationList = new ArrayList<>();
		String sql="select * from MEMBER_NOTIFICATION WHERE MEMBER_ID = ? AND IS_VISIBLE=?";
	try( Connection conn =ds.getConnection();
			PreparedStatement pstmt=conn.prepareStatement(sql);
			){
			pstmt.setInt(1,memberId);
			pstmt.setInt(2,1);
			try(ResultSet rs =pstmt.executeQuery()){
				while(rs.next()) {
					Notification notification=new Notification();
					notification.setMemberNotificationId(rs.getInt("member_notification_id"));
					notification.setNotificationId(rs.getInt("notification_id"));
					notification.setMemberId(rs.getInt("member_id"));
					notification.setIsRead(rs.getInt("is_read"));
					notification.setIsVisible(rs.getInt("is_visible"));
					notification.setNotificationStatus(rs.getInt("notification_status"));
					notification.setTitle(rs.getString("title"));
					notification.setMessage(rs.getString("message"));
					notification.setLinkURL(rs.getString("link_url"));
					notification.setReadTime(rs.getTimestamp("read_time"));
					notification.setSendTime(rs.getTimestamp("send_time"));
					notification.setCreateTime(rs.getTimestamp("create_time"));
					notification.setUpdateTime(rs.getTimestamp("update_time"));
					notificationList.add(notification);
					
					
	
				}
			}
		
	}catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println("查到資料筆數：" + notificationList.size());
	return notificationList;
}

	@Override
	public Integer updateIsRead(int memberId, int memberNotificationId) {
		
		String sql="UPDATE MEMBER_NOTIFICATION SET IS_READ= ?,READ_TIME=? ,UPDATE_TIME =? WHERE MEMBER_ID = ? AND MEMBER_NOTIFICATION_ID=?";
	try( Connection conn =ds.getConnection();
			PreparedStatement pstmt=conn.prepareStatement(sql);
			){
			pstmt.setInt(1,1);
			pstmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			pstmt.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(4,memberId);
			pstmt.setInt(5,memberNotificationId);
			
			int rowsUpdated = pstmt.executeUpdate();
			 if (rowsUpdated > 0) {
				 System.out.println("更新資料筆數：" + rowsUpdated);   
				 return rowsUpdated;
			        
			    } else {
			        return null;
			    }
	
	
		
	}catch(SQLException e) {
		e.printStackTrace();
	}
	

	return null;

	}

	@Override
	public Integer updateUnvisible(int memberNotificationId) {
		
		String sql="UPDATE MEMBER_NOTIFICATION SET IS_VISIBLE= ?,UPDATE_TIME =? WHERE MEMBER_NOTIFICATION_ID=?";
	try( Connection conn =ds.getConnection();
			PreparedStatement pstmt=conn.prepareStatement(sql);
			){
			pstmt.setInt(1,0);
			pstmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(3,memberNotificationId);
			
			int rowsUpdated = pstmt.executeUpdate();
			 if (rowsUpdated > 0) {
				 System.out.println("更新隱藏資料筆數：" + rowsUpdated);   
				 return rowsUpdated;
			        
			    } else {
			        return null;
			    }
	
	
		
	}catch(SQLException e) {
		e.printStackTrace();
	}
	

	return null;

	}
}

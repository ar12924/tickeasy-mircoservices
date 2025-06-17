package user.notify.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.notify.dao.NotificationDao;
import user.notify.vo.Notification;

@Repository
public class NotificationDaoImpl implements NotificationDao {
	private DataSource ds;
	
	@PersistenceContext
	private Session session;
	/*
	 * public NotificationDaoImpl() throws NamingException { ds =(DataSource) new
	 * InitialContext() .lookup("java:comp/env/jdbc/tickeasy"); }
	 */

	@Override
	public List<Notification> selectAllByMemberId(int memberId) {
		List<Notification> notificationList = new ArrayList<>();
	
			/* session = getSession(); */
			String hql = "FROM Notification WHERE memberId=:memberId AND isVisible=1";
			notificationList = session.createQuery(hql, Notification.class).setParameter("memberId", memberId)
					.getResultList();

		
		System.out.println("查到資料筆數：" + notificationList.size());
		return notificationList;
		/*
		 * List<Notification> notificationList = new ArrayList<>(); String
		 * sql="select * from MEMBER_NOTIFICATION WHERE MEMBER_ID = ? AND IS_VISIBLE=?";
		 * try( Connection conn =ds.getConnection(); PreparedStatement
		 * pstmt=conn.prepareStatement(sql); ){ pstmt.setInt(1,memberId);
		 * pstmt.setInt(2,1); try(ResultSet rs =pstmt.executeQuery()){ while(rs.next())
		 * { Notification notification=new Notification();
		 * notification.setMemberNotificationId(rs.getInt("member_notification_id"));
		 * notification.setNotificationId(rs.getInt("notification_id"));
		 * notification.setMemberId(rs.getInt("member_id"));
		 * notification.setIsRead(rs.getInt("is_read"));
		 * notification.setIsVisible(rs.getInt("is_visible"));
		 * notification.setNotificationStatus(rs.getInt("notification_status"));
		 * notification.setTitle(rs.getString("title"));
		 * notification.setMessage(rs.getString("message"));
		 * notification.setLinkURL(rs.getString("link_url"));
		 * notification.setReadTime(rs.getTimestamp("read_time"));
		 * notification.setSendTime(rs.getTimestamp("send_time"));
		 * notification.setCreateTime(rs.getTimestamp("create_time"));
		 * notification.setUpdateTime(rs.getTimestamp("update_time"));
		 * notificationList.add(notification);
		 * 
		 * 
		 * 
		 * } }
		 * 
		 * }catch(SQLException e) { e.printStackTrace(); } System.out.println("查到資料筆數："
		 * + notificationList.size()); return notificationList;
		 */
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

	@Override
	public void sendReminderNotificationForTomorrow() {
		String sql = "SELECT\r\n" 
				+ "bt.current_holder_member_id,\r\n" 
				+ "bo.event_id,\r\n"
				+ "ei.event_name,\r\n" + "ei.event_from_date\r\n"
				+ "FROM buyer_order bo\r\n" 
				+ "JOIN buyer_ticket bt ON bo.order_id = bt.order_id\r\n"
				+ "JOIN event_info ei ON bo.event_id = ei.event_id\r\n"
				+ "WHERE DATEDIFF(ei.event_from_date, CURDATE()) = 1";
		/*
		 * String sql1 ="select event_id,event_from_date\r\n" + "FROM event_info\r\n" +
		 * "WHERE DATEDIFF(event_from_date,NOW())=1";
		 */

		try (Connection conn =ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (!rs.isBeforeFirst()) {
			    System.out.println("⚠️ 查無符合條件的活動資料（明天沒有活動）");
			    return;
			}
			while (rs.next()) {
				
				int memberId = rs.getInt("current_holder_member_id");
				int eventId = rs.getInt("event_id");
				String eventName = rs.getString("event_name");
				Date eventDate = rs.getDate("event_from_date");
				/*
				 * int memberId = rs.getInt("current_holder_member_id"); int eventId =
				 * rs.getInt("event_id");
				 */
				/* Date eventDate = rs.getDate("event_from_date"); */

				// 呼叫發送通知的函式
				sendReminderNotification(memberId, eventId, eventName, eventDate);
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendReminderNotification(int memberId, int eventId, String eventName, Date eventDate) {
		String sql = "INSERT INTO member_notification (notification_id,member_id,is_read,is_visible,notification_status,title,message,link_url,send_time,create_time,update_time) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW())";

		try (Connection conn =ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			// 設置通知的資料
			ps.setInt(1, 5);
			ps.setInt(2, memberId);
			ps.setInt(3, 0);
			ps.setInt(4, 1);
			ps.setInt(5, 1);
			ps.setString(6, "活動提醒");
			ps.setString(7,
					String.format("親愛的會員 %d，您訂購的活動「%s」將於 %s 演出，請記得準時參加！", memberId, eventName, eventDate.toString()));
			ps.setString(8, "/event/" + eventId);

			// 執行插入操作
			int rowsInserted = ps.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("活動提醒通知已成功加入到 member_notification 表");
			} else {
				System.out.println("活動提醒插入通知失敗");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendFavoriteSellReminderNotificationForTomorrow() {
		String sql = "SELECT f.member_id,f.event_id ,eiett.event_name,eiett.sell_from_time ,eiett.sell_to_time ,eiett.category_name \r\n"
				+ "FROM favorite f\r\n"
				+ "JOIN (SELECT ei.event_id,ei.event_name,ett.sell_from_time ,ett.sell_to_time ,ett.category_name FROM event_info ei \r\n"
				+ "JOIN event_ticket_type ett ON ett.event_id=ei.event_id ) AS eiett ON f.event_id=eiett.event_id \r\n"
				+ "WHERE DATEDIFF(eiett.sell_from_time, CURDATE()) = 1 AND f.is_followed = 1\r\n" ;
		 

		try (Connection conn =ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (!rs.isBeforeFirst()) {
			    System.out.println("⚠️ 查無符合條件的關注會員資料（明天沒有活動開賣提醒）");
			    return;
			}
			while (rs.next()) {
				
				int memberId = rs.getInt("member_id");
				int eventId = rs.getInt("event_id");
				String eventName = rs.getString("event_name");
				Date eventSellFromTime = rs.getDate("sell_from_time");
				Date eventSellToTime = rs.getDate("sell_from_time");
				String categoryName = rs.getString("category_name");
				/*
				 * int memberId = rs.getInt("current_holder_member_id"); int eventId =
				 * rs.getInt("event_id");
				 */
				/* Date eventDate = rs.getDate("event_from_date"); */

				// 呼叫發送通知的函式
				sendFavoriteSellReminderNotification(memberId, eventId, eventName, eventSellFromTime,eventSellToTime,categoryName);
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName,
			Date eventSellFromTime, Date eventSellToTime, String categoryName) {
		String sql = "INSERT INTO member_notification (notification_id,member_id,is_read,is_visible,notification_status,title,message,link_url,send_time,create_time,update_time) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW())";

		try (Connection conn =ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			// 設置通知的資料
			ps.setInt(1, 5);
			ps.setInt(2, memberId);
			ps.setInt(3, 0);
			ps.setInt(4, 1);
			ps.setInt(5, 1);
			ps.setString(6, "關注活動開賣提醒");
			ps.setString(7,
					String.format("親愛的會員 %d，您訂購的活動「%s」%s票種 將於 %s 開賣至 %s，請記得準備購買！", memberId, eventName, categoryName,eventSellFromTime.toString(),eventSellToTime.toString()));
			ps.setString(8, "/event/" + eventId);

			// 執行插入操作
			int rowsInserted = ps.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("關注活動開賣提醒通知已成功加入到 member_notification 表");
			} else {
				System.out.println("關注活動開賣提醒插入通知失敗");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
		
	
}

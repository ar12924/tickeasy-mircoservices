package user.notify.dao.impl;

import java.sql.Connection;
import java.sql.Date;
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
	@PersistenceContext
	private Session session;
	
	private DataSource ds;

	public NotificationDaoImpl() throws NamingException {
		ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
	}


	@Override
	public List<Notification> selectAllByMemberId(int memberId) {
		List<Notification> notificationList = new ArrayList<>();
		String hql = "FROM Notification WHERE memberId=:memberId AND isVisible=1";
		notificationList = session
				.createQuery(hql, Notification.class)
				.setParameter("memberId", memberId)
				.getResultList();
		System.out.println("查到資料筆數：" + notificationList.size());
		return notificationList;
	}

	@Override
	public Integer updateIsRead(int memberId, int memberNotificationId) {
		String hql = "UPDATE Notification SET IS_READ= :isRead,READ_TIME=:readTime ,UPDATE_TIME =:updateTime WHERE MEMBER_ID = :memberId AND MEMBER_NOTIFICATION_ID=:memberNotificationId";
		int result = session.createQuery(hql).setParameter("isRead", 1)
				.setParameter("readTime", new Timestamp(System.currentTimeMillis()))
				.setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
				.setParameter("memberId", memberId).setParameter("memberNotificationId", memberNotificationId)
				.executeUpdate();
		if (result > 0) {
			System.out.println("更新資料筆數：" + result);
			return result;
		} else {
			return null;
		}
	}

	@Override
	public Integer updateUnvisible(int memberNotificationId) {
		String hql = "UPDATE Notification SET IS_VISIBLE= :isVisible,UPDATE_TIME =:updateTime WHERE MEMBER_NOTIFICATION_ID=:memberNotificationId";
		int result = session.createQuery(hql)
				.setParameter("isVisible", 0)
				.setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
				.setParameter("memberNotificationId", memberNotificationId)
				.executeUpdate();

		if (result > 0) {
			System.out.println("更新隱藏資料筆數：" + result);
			return result;
		} else {
			return null;
		}
	}

	@Override
	public List<Object[]> sendReminderNotificationForTomorrowList() {
		String sql = "SELECT\r\n" + "bt.current_holder_member_id,\r\n" + "bo.event_id,\r\n" + "ei.event_name,\r\n"
				+ "ei.event_from_date\r\n" + "FROM buyer_order bo\r\n"
				+ "JOIN buyer_ticket bt ON bo.order_id = bt.order_id\r\n"
				+ "JOIN event_info ei ON bo.event_id = ei.event_id\r\n"
				+ "WHERE DATEDIFF(ei.event_from_date, CURDATE()) = 1";
		return session.createNativeQuery(sql/* , Object[].class */).getResultList();
	}

	@Override
	public int sendReminderNotification(int memberId, int eventId, String eventName, Timestamp eventDate) {
//		session.persist(eventDate);
		
		System.out.println("📬 正在寫入通知給 memberId=" + memberId);
		String sql = "INSERT INTO member_notification "
				+ "(notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) "
				+ "VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";

		String message = String.format("親愛的會員 %d，您訂購的活動「%s」將於 %s 演出，請記得準時參加！", memberId, eventName,
				eventDate.toString());

		int result = session.createNativeQuery(sql)
				.setParameter("notificationId", 5) // 或自動遞增可以省略
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", "活動提醒")
				.setParameter("message", message)
				.setParameter("linkUrl", "/event/" + eventId)
				.executeUpdate();
		return result;
		/*
		 * if (result > 0) { System.out.println("✅ 活動提醒通知已成功透過 Hibernate SQL 插入！"); }
		 * else { System.out.println("⚠️ 活動提醒通知插入失敗！"); }
		 */
	}

	/*
	 * @Override public void sendReminderNotification(int memberId, int eventId,
	 * String eventName, Date eventDate) { String sql =
	 * "INSERT INTO member_notification (notification_id,member_id,is_read,is_visible,notification_status,title,message,link_url,send_time,create_time,update_time) "
	 * + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW())";
	 * 
	 * try (Connection conn = ds.getConnection(); PreparedStatement ps =
	 * conn.prepareStatement(sql)) {
	 * 
	 * // 設置通知的資料 ps.setInt(1, 5); ps.setInt(2, memberId); ps.setInt(3, 0);
	 * ps.setInt(4, 1); ps.setInt(5, 1); ps.setString(6, "活動提醒"); ps.setString(7,
	 * String.format("親愛的會員 %d，您訂購的活動「%s」將於 %s 演出，請記得準時參加！", memberId, eventName,
	 * eventDate.toString())); ps.setString(8, "/event/" + eventId);
	 * 
	 * // 執行插入操作 int rowsInserted = ps.executeUpdate(); if (rowsInserted > 0) {
	 * System.out.println("活動提醒通知已成功加入到 member_notification 表"); } else {
	 * System.out.println("活動提醒插入通知失敗"); }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); } }
	 */

	@Override
	public void sendFavoriteSellReminderNotificationForTomorrow() {
		String sql = "SELECT f.member_id,f.event_id ,eiett.event_name,eiett.sell_from_time ,eiett.sell_to_time ,eiett.category_name \r\n"
				+ "FROM favorite f\r\n"
				+ "JOIN (SELECT ei.event_id,ei.event_name,ett.sell_from_time ,ett.sell_to_time ,ett.category_name FROM event_info ei \r\n"
				+ "JOIN event_ticket_type ett ON ett.event_id=ei.event_id ) AS eiett ON f.event_id=eiett.event_id \r\n"
				+ "WHERE DATEDIFF(eiett.sell_from_time, CURDATE()) = 1 AND f.is_followed = 1\r\n";

		try (Connection conn = ds.getConnection();
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
				sendFavoriteSellReminderNotification(memberId, eventId, eventName, eventSellFromTime, eventSellToTime,
						categoryName);

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

		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			// 設置通知的資料
			ps.setInt(1, 5);
			ps.setInt(2, memberId);
			ps.setInt(3, 0);
			ps.setInt(4, 1);
			ps.setInt(5, 1);
			ps.setString(6, "關注活動開賣提醒");
			ps.setString(7, String.format("親愛的會員 %d，您訂購的活動「%s」%s票種 將於 %s 開賣至 %s，請記得準備購買！", memberId, eventName,
					categoryName, eventSellFromTime.toString(), eventSellToTime.toString()));
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

	@Override
	public List<Object[]> sendFavoriteSoldOutReminderList() {
		String sql = "SELECT fi.member_id,mb.user_name, fi.event_id, ei.event_name,ei.event_to_date\r\n"
				+ "FROM favorite fi\r\n"
				+ "JOIN event_info ei ON fi.event_id = ei.event_id\r\n"
				+ "JOIN MEMBER mb ON fi.member_id =mb.member_id\r\n"
				+ "WHERE fi.is_followed=1 AND DATEDIFF(ei.event_to_date, CURDATE()) = -1";
		return session.createNativeQuery(sql).getResultList();
	}

	@Override
	public int sendFavoriteSoldOutReminderNotification(int memberId,String userName,int eventId,String eventName,Timestamp eventToDate) {
		
		String sql = "INSERT INTO member_notification "
				+ "(notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) "
				+ "VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";

		String message = String.format("您關注的活動「%s」售票將於24小時內結束，請把握最後機會！", eventName);
		String message2= String.format("%s 售票即將結束",eventName);
	
		
		int result = session.createNativeQuery(sql)// 或自動遞增可以省略
				.setParameter("notificationId", 4) // 或自動遞增可以省略
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", message2)
				.setParameter("message", message)
				.setParameter("linkUrl", "/event/" + eventId)
				.executeUpdate();
		return result;
	}


	@Override
	public List<Object[]> sendFavoriteLeftPercentReminderList() {
		String sql = "SELECT e1.event_id,e1.cou,e2.cap\r\n"
				+ "FROM(SELECT cc.event_id,COUNT(cc.ticket_id) AS cou\r\n"
				+ "FROM(\r\n"
				+ "SELECT bt.ticket_id,bt.type_id,ett.event_id\r\n"
				+ "FROM buyer_ticket bt\r\n"
				+ "JOIN event_ticket_type ett\r\n"
				+ "ON bt.type_id=ett.type_id \r\n"
				+ "\r\n"
				+ ")AS cc\r\n"
				+ "GROUP BY cc.event_id)AS e1\r\n"
				+ "JOIN(SELECT ett.event_id,SUM(ett.capacity) AS cap\r\n"
				+ "FROM event_ticket_type ett\r\n"
				+ "GROUP BY event_id)AS e2\r\n"
				+ "ON e1.event_id=e2.event_id";
		return session.createNativeQuery(sql).getResultList();
	}


	@Override
	public int sendFavoriteLeftPercentReminderNotification(int memberId, String userName, int eventId,
			String eventName,int percent) {
		String sql = "INSERT INTO member_notification "
				+ "(notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) "
				+ "VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";

		String message = "您關注的活動「" + eventName + "」票券已售出"+percent+"%，剩餘數量有限，請盡快購買！";
		String message2 = eventName + "票券售出已達"+ percent+"%";
	
		
		int result = session.createNativeQuery(sql)// 或自動遞增可以省略
				.setParameter("notificationId", 3) // 或自動遞增可以省略
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", message2)
				.setParameter("message", message)
				.setParameter("linkUrl", "/event/" + eventId)
				.executeUpdate();
		return result;
	}


	@Override
	public List<Object[]> sendFavoriteLeftPercentReminderMemList(int eventId) {
		String sql = "SELECT fi.member_id,mb.user_name, fi.event_id, ei.event_name\r\n"
				+ "FROM favorite fi\r\n"
				+ "JOIN event_info ei ON fi.event_id = ei.event_id\r\n"
				+ "JOIN MEMBER mb ON fi.member_id =mb.member_id\r\n"
				+ "WHERE fi.is_followed=1 AND fi.event_id=:eventId";
		return session
				.createNativeQuery(sql)
				.setParameter("eventId", eventId)
				.getResultList();
	}

}

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
	/*
	 * private DataSource ds;
	 * 
	 * public NotificationDaoImpl() throws NamingException { ds = (DataSource) new
	 * InitialContext().lookup("java:comp/env/jdbc/tickeasy"); }
	 */


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

		String title_template= titleTemplateNotification(5).replace("{event_name}", eventName);
		String message_template= messageTemplateNotification(5).replace("{event_name}", eventName);
		String link_template=linkTemplateNotification(5).replace("{event_id}", eventId+"");
		
		 

		int result = session.createNativeQuery(sql)
				.setParameter("notificationId", 5)
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", title_template)
				.setParameter("message", message_template)
				.setParameter("linkUrl", link_template)
				.executeUpdate();
		return result;
		
	}

	
	@Override
	public List<Object[]> sendFavoriteSellReminderNotificationForTomorrowList() {
		String sql = "SELECT f.member_id,f.event_id ,eiett.event_name,eiett.sell_from_time ,eiett.sell_to_time ,eiett.category_name \r\n"
				+ "FROM favorite f\r\n"
				+ "JOIN (SELECT ei.event_id,ei.event_name,ett.sell_from_time ,ett.sell_to_time ,ett.category_name FROM event_info ei \r\n"
				+ "JOIN event_ticket_type ett ON ett.event_id=ei.event_id ) AS eiett ON f.event_id=eiett.event_id \r\n"
				+ "WHERE DATEDIFF(eiett.sell_from_time, CURDATE()) = 1 AND f.is_followed = 1\r\n";

		return session.createNativeQuery(sql/* , Object[].class */).getResultList();

	}

	@Override
	public int sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName,
			Timestamp eventSellFromTime, Timestamp eventSellToTime, String categoryName) {
		
		String sql = "INSERT INTO member_notification "
				+ "(notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) "
				+ "VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";	
		
		
		
		String message = String.format("親愛的會員，您關注的活動「%s」%s票種 將於 %s 開賣至 %s，請記得準備購買！", eventName,
				categoryName, eventSellFromTime.toString(), eventSellToTime.toString());

		String link_template=linkTemplateNotification(2).replace("{event_id}", eventId+"");
		int result = session.createNativeQuery(sql)
				.setParameter("notificationId", 2)
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", "關注開賣提醒")
				.setParameter("message", message)
				.setParameter("linkUrl", link_template)
				.executeUpdate();
		return result;
		
		
	

	}

	@Override
	public List<Object[]> sendFavoriteSoldOutReminderList() {
		String sql = "SELECT DISTINCT fi.member_id,mb.user_name, fi.event_id, ei.event_name,ett.sell_to_time\r\n"
				+ "FROM favorite fi\r\n"
				+ "JOIN event_info ei ON fi.event_id = ei.event_id\r\n"
				+ "JOIN MEMBER mb ON fi.member_id =mb.member_id\r\n"
				+ "JOIN event_ticket_type ett ON ett.event_id=fi.event_id\r\n"
				+ "WHERE fi.is_followed=1 AND DATEDIFF(ett.sell_to_time, CURDATE()) = 1";
		return session.createNativeQuery(sql).getResultList();
	}

	@Override
	public int sendFavoriteSoldOutReminderNotification(int memberId,String userName,int eventId,String eventName,Timestamp eventToDate) {
		
		String sql = "INSERT INTO member_notification "
				+ "(notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) "
				+ "VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";
		
		String message = String.format("您關注的活動「%s」售票將於24小時內結束，請把握最後機會！", eventName);
		String message2= String.format("%s 售票即將結束",eventName);
	
		
		int result = session.createNativeQuery(sql)
				.setParameter("notificationId", 4) 
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", message2)
				.setParameter("message", message)
				.setParameter("linkUrl", "/user/buy/event_ticket_purchase.html?eventId=" + eventId)
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
	
		
		int result = session.createNativeQuery(sql)
				.setParameter("notificationId", 3) 
				.setParameter("memberId", memberId)
				.setParameter("isRead", 0)
				.setParameter("isVisible", 1)
				.setParameter("status", 1)
				.setParameter("title", message2)
				.setParameter("message", message)
				.setParameter("linkUrl", "/user/buy/event_ticket_purchase.html?eventId=" + eventId)
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
	


	@Override
	public String titleTemplateNotification(int notificationId) {
		String sql = "SELECT nt.title_template\r\n"
				+ "FROM notification_template nt\r\n"
				+ "WHERE nt.notification_id=:notificationId";
		return (String)session
				.createNativeQuery(sql)
				.setParameter("notificationId", notificationId)
				.getSingleResult();
	}

	@Override
	public String messageTemplateNotification(int notificationId) {
		String sql = "SELECT nt.message_template\r\n"
				+ "FROM notification_template nt\r\n"
				+ "WHERE nt.notification_id=:notificationId";
		return (String)session
				.createNativeQuery(sql)
				.setParameter("notificationId", notificationId)
				.getSingleResult();
	}

	@Override
	public String linkTemplateNotification(int notificationId) {
		String sql = "SELECT nt.link_url\r\n"
				+ "FROM notification_template nt\r\n"
				+ "WHERE nt.notification_id=:notificationId";
		return (String)session
				.createNativeQuery(sql)
				.setParameter("notificationId", notificationId)
				.getSingleResult();
	}

	

}

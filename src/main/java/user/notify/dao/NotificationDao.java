package user.notify.dao;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;


import common.util.HibernateUtil5;
import user.notify.vo.Notification;

public interface NotificationDao {

	List<Notification> selectAllByMemberId(int memberId);
	Integer updateIsRead(int memberId, int memberNotificationId);
	Integer updateUnvisible(int memberNotificationId);
	List<Object[]> sendReminderNotificationForTomorrowList();
	int sendReminderNotification(int memberId, int eventId, String eventName, Timestamp eventDate);
	void sendFavoriteSellReminderNotificationForTomorrow();
	void sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName, Date eventSellFromTime,Date eventSellToTime,String categoryName);
	List<Object[]> sendFavoriteSoldOutReminderList();
	int sendFavoriteSoldOutReminderNotification(int memberId,String userName,int eventId,String eventName,Timestamp eventToDate);
	List<Object[]> sendFavoriteLeftPercentReminderList();
	int sendFavoriteLeftPercentReminderNotification(int memberId,String userName,int eventId,String eventName,int percent);
	List<Object[]> sendFavoriteLeftPercentReminderMemList(int eventId);
	
	
	default Session getSession() {
		return HibernateUtil5
				.getSessionFactory()
				.getCurrentSession();
	//			.openSession();
	}
}

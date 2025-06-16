package user.notify.dao;

import java.sql.Date;
import java.util.List;

import org.hibernate.Session;


import common.util.HibernateUtil5;
import user.notify.vo.Notification;

public interface NotificationDao {

	List<Notification> selectAllByMemberId(int memberId);
	Integer updateIsRead(int memberId, int memberNotificationId);
	Integer updateUnvisible(int memberNotificationId);
	void sendReminderNotificationForTomorrow();
	void sendReminderNotification(int memberId, int eventId, String eventName, Date eventDate);
	void sendFavoriteSellReminderNotificationForTomorrow();
	void sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName, Date eventSellFromTime,Date eventSellToTime,String categoryName);
	default Session getSession() {
		return HibernateUtil5
				.getSessionFactory()
				.getCurrentSession();
	//			.openSession();
	}
}

package user.notify.dao;


import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;

import common.util.HibernateUtil5;
import user.notify.vo.Notification;

public interface NotificationDao {

	List<Notification> selectAllByMemberId(int memberId);
	Integer updateIsRead(int memberId, int memberNotificationId);
	Integer updateUnvisible(int memberNotificationId);
	Integer updateListClear(int memberId);
	//活動提醒
	List<Object[]> sendReminderNotificationForTomorrowList();
	int sendReminderNotification(int memberId, int eventId, String eventName, Timestamp eventDate);
	//關注開賣提醒
	List<Object[]> sendFavoriteSellReminderNotificationForTomorrowList();
	int sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName, Timestamp eventSellFromTime,Timestamp eventSellToTime,String categoryName);
	//關注售完提醒
	List<Object[]> sendFavoriteSoldOutReminderList();
	int sendFavoriteSoldOutReminderNotification(int memberId,String userName,int eventId,String eventName,Timestamp eventToDate);
	//關注剩餘提醒
	List<Object[]> sendFavoriteLeftPercentReminderList();
	int sendFavoriteLeftPercentReminderNotification(int memberId,String userName,int eventId,String eventName,int percent);
	List<Object[]> sendFavoriteLeftPercentReminderMemList(int eventId);
	

	//通知模版所需
	String titleTemplateNotification(int notificationId);
	String messageTemplateNotification(int notificationId);
	String linkTemplateNotification(int notificationId);
	
	
	default Session getSession() {
		return HibernateUtil5
				.getSessionFactory()
				.getCurrentSession();
	//			.openSession();
	}
}

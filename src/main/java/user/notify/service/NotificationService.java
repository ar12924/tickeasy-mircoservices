package user.notify.service;

import java.util.List;

import user.notify.vo.Notification;

public interface NotificationService {

	List<Notification> notificationList(int memberId);
	Integer notificationRead(int memberId,int memberNotificationId);
	Integer notificationVisibleUpdate(int memberNotificationId);
	Integer notificationListClearUpdate(int memberId);
	
	void sendReminderNotificationForTomorrow();
	void sendFavoriteSellReminderNotificationForTomorrow();
	void sendFavoriteSoldOutReminderNotification();
	void sendFavoriteLeftPercentReminderNotification();
	//Redis
	boolean isAlreadyNotifiedFavoriteLeftPercent(Integer eventId,Integer memberId,String type);
	void markAsNotifiedFavoriteLeftPercent(Integer eventId,Integer memberId,String type);
}

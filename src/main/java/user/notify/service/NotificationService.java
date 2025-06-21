package user.notify.service;

import java.sql.Date;
import java.util.List;

import user.notify.vo.Notification;

public interface NotificationService {

	List<Notification> notificationList(int memberId);
	Integer notificationRead(int memberId,int memberNotificationId);
	Integer notificationVisibleUpdate(int memberNotificationId);
	void sendReminderNotificationForTomorrow();
	void sendFavoriteSellReminderNotificationForTomorrow();
	void sendFavoriteSoldOutReminderNotification();
}

package user.notify.service;

import java.util.List;

import user.notify.vo.Notification;

public interface NotificationService {

	List<Notification> notificationList(int memberId);
	Integer notificationRead(int memberId,int memberNotificationId);
	Integer notificationVisibleUpdate(int memberNotificationId);
}

package microservices.notify.dao;

import microservices.notify.vo.Notification;
import java.sql.Timestamp;
import java.util.List;

public interface NotificationDao {
    List<Notification> selectAllByMemberId(int memberId);
    Integer updateIsRead(int memberId, int memberNotificationId);
    Integer updateUnvisible(int memberNotificationId);
    Integer updateListClear(int memberId);

    // 排程所需查詢/插入
    List<Object[]> sendReminderNotificationForTomorrowList();
    int sendReminderNotification(int memberId, int eventId, String eventName, Timestamp eventDate);

    List<Object[]> sendFavoriteSellReminderNotificationForTomorrowList();
    int sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName, Timestamp eventSellFromTime, Timestamp eventSellToTime, String categoryName);

    List<Object[]> sendFavoriteSoldOutReminderList();
    int sendFavoriteSoldOutReminderNotification(int memberId, String userName, int eventId, String eventName, Timestamp eventToDate);

    List<Object[]> sendFavoriteLeftPercentReminderList();
    int sendFavoriteLeftPercentReminderNotification(int memberId, String userName, int eventId, String eventName, int percent);
    List<Object[]> sendFavoriteLeftPercentReminderMemList(int eventId);

    // 模板
    String titleTemplateNotification(int notificationId);
    String messageTemplateNotification(int notificationId);
    String linkTemplateNotification(int notificationId);
}



package microservices.notify.service.impl;

import microservices.notify.dao.NotificationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Component
public class NotificationScheduler {
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    @Resource
    private NotificationDao notificationDao;

    // 每天 10:00 提醒明日活動（可由 application-*.yml 覆寫）
    @Scheduled(cron = "${notify.cron.reminder:0 0 10 * * *}")
    public void sendTomorrowEventReminders() {
        List<Object[]> rows = notificationDao.sendReminderNotificationForTomorrowList();
        for (Object[] r : rows) {
            Integer memberId = ((Number) r[0]).intValue();
            Integer eventId = ((Number) r[1]).intValue();
            String eventName = (String) r[2];
            Timestamp eventDate = (Timestamp) r[3];
            int inserted = notificationDao.sendReminderNotification(memberId, eventId, eventName, eventDate);
            log.info("TomorrowReminder inserted={} memberId={} eventId={}", inserted, memberId, eventId);
        }
    }

    // 每天 11:00 提醒收藏的明日開賣
    @Scheduled(cron = "${notify.cron.sell:0 0 11 * * *}")
    public void sendFavoriteSellReminders() {
        List<Object[]> rows = notificationDao.sendFavoriteSellReminderNotificationForTomorrowList();
        for (Object[] r : rows) {
            Integer memberId = ((Number) r[0]).intValue();
            Integer eventId = ((Number) r[1]).intValue();
            String eventName = (String) r[2];
            Timestamp sellFrom = (Timestamp) r[3];
            Timestamp sellTo = (Timestamp) r[4];
            String categoryName = (String) r[5];
            int inserted = notificationDao.sendFavoriteSellReminderNotification(memberId, eventId, eventName, sellFrom, sellTo, categoryName);
            log.info("FavoriteSellReminder inserted={} memberId={} eventId={}", inserted, memberId, eventId);
        }
    }

    // 每天 12:00 提醒收藏的最後一天
    @Scheduled(cron = "${notify.cron.soldout:0 0 12 * * *}")
    public void sendFavoriteSoldoutReminders() {
        List<Object[]> rows = notificationDao.sendFavoriteSoldOutReminderList();
        for (Object[] r : rows) {
            Integer memberId = ((Number) r[0]).intValue();
            String userName = (String) r[1];
            Integer eventId = ((Number) r[2]).intValue();
            String eventName = (String) r[3];
            Timestamp toDate = (Timestamp) r[4];
            int inserted = notificationDao.sendFavoriteSoldOutReminderNotification(memberId, userName, eventId, eventName, toDate);
            log.info("FavoriteSoldOutReminder inserted={} memberId={} eventId={}", inserted, memberId, eventId);
        }
    }

    // 每天 13:00 提醒剩餘百分比門票
    @Scheduled(cron = "${notify.cron.leftPercent:0 0 13 * * *}")
    public void sendLeftPercentReminders() {
        List<Object[]> rows = notificationDao.sendFavoriteLeftPercentReminderList();
        for (Object[] r : rows) {
            Integer eventId = ((Number) r[0]).intValue();
            Integer count = ((Number) r[1]).intValue();
            Integer cap = ((Number) r[2]).intValue();
            if (cap == 0) continue;
            int leftPercent = Math.max(0, 100 - (int) Math.round(count * 100.0 / cap));
            if (leftPercent == 0 || leftPercent == 100) continue;
            List<Object[]> mems = notificationDao.sendFavoriteLeftPercentReminderMemList(eventId);
            for (Object[] m : mems) {
                Integer memberId = ((Number) m[0]).intValue();
                String userName = (String) m[1];
                String eventName = (String) m[3];
                int inserted = notificationDao.sendFavoriteLeftPercentReminderNotification(memberId, userName, eventId, eventName, leftPercent);
                log.info("LeftPercentReminder inserted={} memberId={} eventId={} left={}"
                        , inserted, memberId, eventId, leftPercent);
            }
        }
    }
}




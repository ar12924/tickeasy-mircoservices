package common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import user.notify.service.NotificationService;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
	
	@Autowired
	private NotificationService notificationService;
	
	/* private ScheduledExecutorService scheduler; */

	
	
	@Scheduled(initialDelay = 60000, fixedRate = 86400000)
    public void EventReminder() {
        System.out.println("🔔 EventReminderTask：開始執行schduled排程任務");
        notificationService.sendReminderNotificationForTomorrow();
    }
	
	@Scheduled(initialDelay = 600000, fixedRate = 86400000)
    public void FavoriteLeftPercentReminder() {
        System.out.println("🔔 FavoriteLeftPercentReminderTask：開始執行schduled排程任務");
        notificationService.sendFavoriteLeftPercentReminderNotification();
    }
	
	
	@Scheduled(initialDelay = 600000, fixedRate = 86400000)
    public void FavoriteSellReminder() {
        System.out.println("🔔 FavoriteSellReminderTask：開始執行schduled排程任務");
        notificationService.sendFavoriteSellReminderNotificationForTomorrow();
    }
	
	@Scheduled(initialDelay = 60000, fixedRate = 1 * 60 * 1000)
    public void FavoriteSoldOutReminder() {
        System.out.println("🔔 FavoriteSoldOutReminderTask：開始執行schduled排程任務");
        notificationService.sendFavoriteSoldOutReminderNotification();
    }

}

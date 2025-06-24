package user.notify.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import user.notify.service.NotificationService;


@Component
public class FavoriteLeftPercentReminderListener {

	@Autowired
	private NotificationService notificationService;
	
	/* private ScheduledExecutorService scheduler; */

	
	
	@Scheduled(initialDelay = 60000, fixedRate = 5 * 60 * 1000)
    public void sendReminder() {
        System.out.println("🔔 FavoriteLeftPercentReminderTask：開始執行schduled排程任務");
        notificationService.sendFavoriteLeftPercentReminderNotification();
    }
	
	
	/*
	 * @PostConstruct public void startScheduler(){
	 * System.out.println("✅ EventReminderListener：啟動排程中...");
	 * 
	 * }
	 */
		
		/*
		
		
		
		scheduler = Executors.newSingleThreadScheduledExecutor();

		// 每 24 小時執行一次（你可用 getInitialDelay() 精確指定起始時間）
		
		 // scheduler.scheduleAtFixedRate(notificationService::
		// sendReminderNotificationForTomorrow, getInitialDelay(), 24, TimeUnit.HOURS);
		 
		scheduler.scheduleAtFixedRate(()->{
			notificationService.sendReminderNotificationForTomorrow();
		}, 1, 24 * 60,
				TimeUnit.MINUTES);*/

	
/*
	@PreDestroy
	public void stopScheduler() {
		System.out.println("🛑 EventReminderListener：關閉排程");
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}

*/










/*



@WebListener
public class EventReminderListener implements ServletContextListener {


	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("✅ EventReminderListener：啟動排程中...");

		scheduler = Executors.newSingleThreadScheduledExecutor();

		// 每 24 小時執行一次（你可用 getInitialDelay() 精確指定起始時間）
		
		 //scheduler.scheduleAtFixedRate(notificationService::
		 //sendReminderNotificationForTomorrow, getInitialDelay(), 24, TimeUnit.HOURS);
		 
		scheduler.scheduleAtFixedRate(()->{
			new NotificationServiceImpl().sendReminderNotificationForTomorrow();
		}, 1, 24 * 60,
				TimeUnit.MINUTES);

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("🛑 EventReminderListener：關閉排程");
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}
*/
	private long getInitialDelay() {
		// 設定每天 00:00 AM 這個時間

		long currentMillis = System.currentTimeMillis();
		long targetMillis = currentMillis - currentMillis % (24 * 60 * 60 * 1000) + 24 * 60 * 60 * 1000;
		return targetMillis - currentMillis;

		/*
		 * long currentMillis = System.currentTimeMillis();
		 * 
		 * // 計算今天午夜的毫秒時間（00:00） long millisPerDay = 24 * 60 * 60 * 1000; long
		 * todayMidnight = currentMillis - (currentMillis % millisPerDay);
		 * 
		 * // 計算今天 00:15 的時間（15分鐘 * 60秒 * 1000毫秒） long targetMillis = todayMidnight + 15
		 * * 60 * 1000;
		 * 
		 * if (currentMillis > targetMillis) { // 如果現在時間已經超過今天 00:15，則目標時間改成明天 00:15
		 * targetMillis += millisPerDay; }
		 * 
		 * return targetMillis - currentMillis;
		 */
	}
}

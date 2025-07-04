package user.notify.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.notify.dao.NotificationDao;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;


@Service
public class NotificationServiceImpl implements NotificationService {
	private final static Logger logger = LogManager.getLogger(NotificationServiceImpl.class);
	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Transactional
	@Override
	public List<Notification> notificationList(int memberId) {
		return notificationDao.selectAllByMemberId(memberId);
	}

	@Transactional
	@Override
	public Integer notificationRead(int memberId, int memberNotificationId) {
		return notificationDao.updateIsRead(memberId, memberNotificationId);
	}

	@Transactional
	@Override
	public Integer notificationVisibleUpdate(int memberNotificationId) {
		return notificationDao.updateUnvisible(memberNotificationId);
	}

	@Transactional
	@Override
	public void sendReminderNotificationForTomorrow() {
		List<Object[]> resultList = notificationDao.sendReminderNotificationForTomorrowList();

		// TODO ????
		logger.info("Reminder排程動");

		if (resultList.isEmpty()) {
			System.out.println("⚠️ 查無符合條件的活動資料（明天沒有活動）");
			return;
		}
		System.out.println("✅ 查到資料筆數：" + resultList.size());

		for (Object[] row : resultList) {
			System.out.println("🔁 處理 row: " + Arrays.toString(row));
			Integer memberId = ((Number) row[0]).intValue();
			Integer eventId = ((Number) row[1]).intValue();
			String eventName = (String) row[2];
			Timestamp eventDate = (Timestamp) row[3];

			System.out.println("有查到資料,要跑方法了");
			int result = notificationDao.sendReminderNotification(memberId, eventId, eventName, eventDate);

			if (result > 0) {
				System.out.println("✅ 活動提醒通知已成功透過 Hibernate SQL 插入！");
			} else {
				System.out.println("⚠️ 活動提醒通知插入失敗！");
			}
		}
	}

	@Transactional
	@Override
	public void sendFavoriteSellReminderNotificationForTomorrow() {
		
		
		
		
		List<Object[]> resultList = notificationDao.sendFavoriteSellReminderNotificationForTomorrowList();

		// TODO ????
		logger.info("Reminder排程動");

		if (resultList.isEmpty()) {
			System.out.println("⚠️ 查無符合條件的活動資料（明天沒有活動）");
			return;
		}
		System.out.println("✅ 查到資料筆數：" + resultList.size());

		for (Object[] row : resultList) {
			System.out.println("🔁 處理 row: " + Arrays.toString(row));
			Integer memberId = ((Number) row[0]).intValue();
			Integer eventId = ((Number) row[1]).intValue();
			String eventName = (String) row[2];
			Timestamp eventSellFromTime = (Timestamp) row[3];
			Timestamp eventSellToTime = (Timestamp) row[4];
			String categoryName=(String) row[5];
			

			System.out.println("有查到資料,要跑方法了");
			int result = notificationDao.sendFavoriteSellReminderNotification(memberId, eventId, eventName, eventSellFromTime, eventSellToTime, categoryName);

			if (result > 0) {
				System.out.println("✅ 關注開賣通知已成功透過 Hibernate SQL 插入！");
			} else {
				System.out.println("⚠️ 關注開賣通知插入失敗！");
			}
		}
	}

	@Transactional
	@Override
	public void sendFavoriteSoldOutReminderNotification() {
		List<Object[]> resultList = notificationDao.sendFavoriteSoldOutReminderList();

		// TODO ????
		logger.info("Reminder排程動");

		if (resultList.isEmpty()) {
			System.out.println("⚠️ 查無符合條件的我的關注資料");
			return;
		}
		System.out.println("✅ 查到資料筆數：" + resultList.size());

		for (Object[] row : resultList) {
			System.out.println("🔁 處理 row: " + Arrays.toString(row));
			Integer memberId = ((Number) row[0]).intValue();
			String userName= (String)row[1];
			Integer eventId = ((Number) row[2]).intValue();
			String eventName = (String) row[3];
			Timestamp eventToDate = (Timestamp) row[4];

			System.out.println("有查到資料,要跑方法了");
			int result = notificationDao.sendFavoriteSoldOutReminderNotification(memberId,userName, eventId, eventName, eventToDate);

			if (result > 0) {
				System.out.println("✅ 售票截止提醒通知已成功透過 Hibernate SQL 插入！");
			} else {
				System.out.println("⚠️ 售票截止提醒通知插入失敗！");
			}
		}
		
	}
	
	@Transactional
	@Override
	public void sendFavoriteLeftPercentReminderNotification() {
		List<Object[]> resultList = notificationDao.sendFavoriteLeftPercentReminderList();
		

		// TODO ????
		logger.info("Reminder排程動");

		if (resultList.isEmpty()) {
			System.out.println("⚠️ 查無符合條件的我的關注資料");
			return;
		}
		

		for (Object[] row : resultList) {
			Integer eventId = ((Number) row[0]).intValue();
			Integer eventCount= ((Number) row[1]).intValue();
			Integer eventCapcity = ((Number) row[2]).intValue();
			Double eventPercent= (double)eventCount/eventCapcity;
			Double percentLeft=1-eventPercent;

			System.out.println("設定一下多少%要寄通知");
			if(0.2< percentLeft && percentLeft <0.6) {
				System.out.println("有60%");
				List<Object[]> resultMem=notificationDao.sendFavoriteLeftPercentReminderMemList(eventId);
			
			for (Object[] row1 : resultMem) {
				Integer memberId = ((Number) row1[0]).intValue();
				String userName= (String)row1[1];
				Integer eventId1 = ((Number) row1[2]).intValue();
				String eventName = (String) row1[3];
				
				String notifyType="SOLD_40";
				 if (isAlreadyNotifiedFavoriteLeftPercent(eventId1, memberId, notifyType)) {
	                    System.out.printf("🚫 已通知過SOLD_40 memberId=%d, eventId=%d，略過\n", memberId, eventId1);
	                    continue;
	                }
			System.out.println("跑到這裡了");
			int result = notificationDao.sendFavoriteLeftPercentReminderNotification(memberId,userName, eventId1, eventName,40);
			
			if (result > 0) {
				System.out.println("✅ 剩餘票券60%提醒通知已成功透過 Hibernate SQL 插入！");
				markAsNotifiedFavoriteLeftPercent(eventId1, memberId, notifyType); // ➤ 寫入 Redis
			} else {
				System.out.println("⚠️ 剩餘票券60%提醒通知插入失敗！");
			}
			}
			}
			if(percentLeft <0.2) {
				System.out.println("有80%");
				List<Object[]> resultMem=notificationDao.sendFavoriteLeftPercentReminderMemList(eventId);
			
			for (Object[] row1 : resultMem) {
				Integer memberId = ((Number) row1[0]).intValue();
				String userName= (String)row1[1];
				Integer eventId1 = ((Number) row1[2]).intValue();
				String eventName = (String) row1[3];
				
				String notifyType="SOLD_80";
				 if (isAlreadyNotifiedFavoriteLeftPercent(eventId1, memberId, notifyType)) {
	                    System.out.printf("🚫 已通知SOLD_80 memberId=%d, eventId=%d，略過\n", memberId, eventId1);
	                    continue;
	                }
				
			System.out.println("跑到這裡了");
			int result = notificationDao.sendFavoriteLeftPercentReminderNotification(memberId,userName, eventId1, eventName,80);
			
			if (result > 0) {
				System.out.println("✅ 剩餘票券20%提醒通知已成功透過 Hibernate SQL 插入！");
				markAsNotifiedFavoriteLeftPercent(eventId1, memberId, notifyType); // ➤ 寫入 Redis
			} else {
				System.out.println("⚠️ 剩餘票券20%提醒通知插入失敗！");
			}
			}
			}
			
			
		}
		
	}

	@Override
	public boolean isAlreadyNotifiedFavoriteLeftPercent(Integer eventId, Integer memberId, String type) {
		String redisKey=String.format("notified:%d:%d:%s",eventId,memberId,type);
		return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
	}

	@Override
	public void markAsNotifiedFavoriteLeftPercent(Integer eventId, Integer memberId, String type) {
		String redisKey = String.format("notified:%d:%d:%s", eventId, memberId,type);
        redisTemplate.opsForValue().set(redisKey, "1", Duration.ofDays(30)); // 存 30 天
		
	}
	/*
	 * @PostConstruct public void testRedisConnection() {
	 * redisTemplate.opsForValue().set("hello", "world"); String result = (String)
	 * redisTemplate.opsForValue().get("hello"); System.out.println("Redis 測試結果：" +
	 * result); }
	 */
}

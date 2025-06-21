package user.notify.service.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
		notificationDao.sendFavoriteSellReminderNotificationForTomorrow();
		System.out.println("Favorite排程動了");
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
}

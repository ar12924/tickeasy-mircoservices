package user.notify.service.impl;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.util.HibernateUtil5;
import user.notify.dao.NotificationDao;
import user.notify.dao.impl.NotificationDaoImpl;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationDao notificationDao;

	/*
	 * public NotificationServiceImpl() throws NamingException { notificationDao =
	 * new NotificationDaoImpl(); }
	 */

	@Transactional
	@Override
	public List<Notification> notificationList(int memberId) {
		return notificationDao.selectAllByMemberId(memberId);
		/*
		 * List<Notification> result = null;
		 * 
		 * result = notificationDao.selectAllByMemberId(memberId);
		 */
		/* Transaction tx = null; Session session = null; */

		/* try { */
		/* session = HibernateUtil5.getSessionFactory().getCurrentSession(); */
		/* tx = session.beginTransaction(); */
		/* result = notificationDao.selectAllByMemberId(memberId); */
		/* tx.commit(); */
		/* } catch (Exception e) { */
		/*
		 * session.getTransaction().rollback();
		 */
		/* } */
		/* return result; */
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

		List<Object[]> resultList=notificationDao.sendReminderNotificationForTomorrowList();
		
		System.out.println("Reminder排程動了");
		
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
		        
		        try {
		        	  System.out.println("有查到資料,要跑方法了");
		        int result= notificationDao.sendReminderNotification(memberId, eventId, eventName, eventDate);
		    
		        if (result > 0) {
			        System.out.println("✅ 活動提醒通知已成功透過 Hibernate SQL 插入！");
			    } else {
			        System.out.println("⚠️ 活動提醒通知插入失敗！");
			    }
		        } catch (Exception e) {
		            System.err.println("🔥 寫入通知錯誤：" + e.getMessage());
		        }
		        
		       
		    }

	}

	@Transactional
	@Override
	public void sendFavoriteSellReminderNotificationForTomorrow() {
		notificationDao.sendFavoriteSellReminderNotificationForTomorrow();
		System.out.println("Favorite排程動了");
		
	}

}

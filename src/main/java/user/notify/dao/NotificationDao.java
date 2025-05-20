package user.notify.dao;

import java.util.List;

import org.hibernate.Session;


import common.util.HibernateUtil5;
import user.notify.vo.Notification;

public interface NotificationDao {

	List<Notification> selectAllByMemberId(int memberId);
	Integer updateIsRead(int memberId, int memberNotificationId);
	Integer updateUnvisible(int memberNotificationId);
	default Session getSession() {
		return HibernateUtil5
				.getSessionFactory()
				.getCurrentSession();
	//			.openSession();
	}
}

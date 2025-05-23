package user.notify.service.impl;

import java.util.List;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import common.util.HibernateUtil5;
import user.notify.dao.NotificationDao;
import user.notify.dao.impl.NotificationDaoImpl;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;

public class NotificationServiceImpl implements NotificationService {

	private NotificationDao notificationDao;

	public NotificationServiceImpl() throws NamingException {
		notificationDao = new NotificationDaoImpl();
	}

	@Override
	public List<Notification> notificationList(int memberId) {
		/* return notificationDao.selectAllByMemberId(memberId); */
		List<Notification> result = null;
		
		 Transaction tx = null; Session session = null;
		 
		try {
			session = HibernateUtil5.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			result = notificationDao.selectAllByMemberId(memberId);
			tx.commit();
		} catch (Exception e) {
			
			session.getTransaction().rollback();
		}
		return result;
	}

	@Override
	public Integer notificationRead(int memberId, int memberNotificationId) {

		return notificationDao.updateIsRead(memberId, memberNotificationId);
	}

	@Override
	public Integer notificationVisibleUpdate(int memberNotificationId) {
		return notificationDao.updateUnvisible(memberNotificationId);
	}

}

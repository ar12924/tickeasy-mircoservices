package user.notify.service.impl;


import java.util.List;

import javax.naming.NamingException;

import user.notify.dao.NotificationDao;
import user.notify.dao.impl.NotificationDaoImpl;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;



public class NotificationServiceImpl implements NotificationService {

	private NotificationDao notificationDao;
	public NotificationServiceImpl() throws NamingException{
		notificationDao =new NotificationDaoImpl();
	}
	@Override
	public List<Notification> notificationList(int memberId) {
		return notificationDao.selectAllByMemberId(memberId);
	}
	@Override
	public Integer notificationRead(int memberId, int memberNotificationId) {
		
		return notificationDao.updateIsRead(memberId,memberNotificationId);
	}
	@Override
	public Integer notificationVisibleUpdate(int memberNotificationId) {
		return notificationDao.updateUnvisible(memberNotificationId);
	}

}

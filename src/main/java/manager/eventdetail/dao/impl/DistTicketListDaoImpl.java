package manager.eventdetail.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.eventdetail.dao.DistTicketListDao;
import manager.eventdetail.vo.DistTicket;
import user.notify.dao.NotificationDao;
import user.notify.vo.Notification;

@Repository
public class DistTicketListDaoImpl implements DistTicketListDao {

	@PersistenceContext
	private Session session;
	
	@Override
	public List<DistTicket> selectAllDistTicketList() {
		List<DistTicket> distTicketList = new ArrayList<>();
		String hql = "SELECT dt FROM DistTicket dt JOIN FETCH dt.buyerOrder";
				
		
	
		distTicketList = session
				.createQuery(hql, DistTicket.class)
				.getResultList();
		return distTicketList;
	}

}


package manager.eventdetail.dao.impl;

import java.sql.Timestamp;
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
	public List<DistTicket> selectAllDistTicketList(Timestamp startTime,Timestamp endTime,Integer selectedEventId) {
		List<DistTicket> distTicketList = new ArrayList<>();
		String hql = "SELECT dt FROM DistTicket dt JOIN FETCH dt.buyerOrder "
				+ "WHERE dt.distedTime BETWEEN :startTime AND :endTime AND dt.buyerOrder.eventId=:eventId";
				
		
	
		distTicketList = session
				.createQuery(hql, DistTicket.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime)
				.setParameter("eventId",selectedEventId)
				.getResultList();
		return distTicketList;
	}

}


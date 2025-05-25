package user.ticket.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;


import user.notify.dao.NotificationDao;
import user.notify.dao.impl.NotificationDaoImpl;
import user.notify.vo.Notification;
import user.ticket.dao.TicketDao;
import user.ticket.dao.impl.TicketDaoImpl;
import user.ticket.service.TicketService;
import user.ticket.vo.Ticket;

public class TicketServiceImpl implements TicketService{

	private TicketDao ticketDao;

	public TicketServiceImpl() throws NamingException {
		ticketDao = new TicketDaoImpl();
	}

	

	@Override
	public List<Ticket> ticketList(int memberId) {
		
		List<Ticket> result = new ArrayList<>();

		result = ticketDao.selectAllByMemberId(memberId);
		
		return result;
		
	}

}

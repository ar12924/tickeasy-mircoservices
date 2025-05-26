package user.ticket.service;

import java.util.List;

import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;

public interface TicketService {
	List<TicketView> ticketList(int memberId);
}

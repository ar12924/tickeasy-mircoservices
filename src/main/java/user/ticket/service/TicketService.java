package user.ticket.service;

import java.util.List;

import user.ticket.vo.Ticket;

public interface TicketService {
	List<Ticket> ticketList(int memberId);
}

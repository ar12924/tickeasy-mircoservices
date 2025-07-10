package user.ticket.dao;

import java.util.List;

import user.ticket.vo.Ticket;

public interface TicketDao {

	List<Ticket> selectAllByMemberId(int memberId);
	List<Ticket> selectAllChangeByMemberId(int memberId);
	Ticket selectTicketByTicketId(int ticketId);
	Integer updateTicketStatus(int ticketId);
}

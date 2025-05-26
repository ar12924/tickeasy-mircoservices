package user.ticket.dao;

import java.util.List;

import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;

public interface TicketDao {

	List<TicketView> selectAllByMemberId(int memberId);
}

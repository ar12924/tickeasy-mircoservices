package user.ticket.service;

import java.util.List;

import user.ticket.dto.TicketViewDto;

public interface TicketService {
	List<TicketViewDto> ticketList(int memberId);
	
}

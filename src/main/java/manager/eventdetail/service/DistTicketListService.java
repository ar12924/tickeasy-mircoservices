package manager.eventdetail.service;

import java.sql.Timestamp;
import java.util.List;

import manager.eventdetail.vo.DistTicket;

public interface DistTicketListService {
	
	List<DistTicket> distTicketListService(Timestamp startTime,Timestamp endTime,Integer selectedEventId);

}

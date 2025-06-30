package manager.eventdetail.dao;

import java.util.List;

import manager.eventdetail.vo.DistTicket;

public interface DistTicketListDao {
	List<DistTicket> selectAllDistTicketList();

}

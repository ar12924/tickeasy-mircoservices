package manager.eventdetail.dao;

import java.sql.Timestamp;
import java.util.List;

import manager.eventdetail.vo.DistTicket;

public interface DistTicketListDao {
	List<DistTicket> selectAllDistTicketList(Timestamp startTime,Timestamp endTime);

}

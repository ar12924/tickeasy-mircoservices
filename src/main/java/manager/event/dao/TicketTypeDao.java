package manager.event.dao;

import java.util.List;
import common.dao.CommonDao;
import manager.event.vo.EventTicketType;

public interface TicketTypeDao extends CommonDao {
    
    public List<EventTicketType> findByEventId(Integer eventId);
    
    public EventTicketType findById(Integer typeId);
    
    public int createTicketType(EventTicketType ticketType);
    
    public int updateTicketType(EventTicketType ticketType);
    
    public int deleteTicketType(Integer typeId);
}
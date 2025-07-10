package manager.event.service;

import java.util.List;
import manager.event.vo.EventTicketType;

public interface TicketTypeService {
    
    public List<EventTicketType> findTicketTypesByEventId(Integer eventId);
    
    public EventTicketType findTicketTypeById(Integer typeId);
    
    public int createTicketType(EventTicketType ticketType);
    
    public int updateTicketType(EventTicketType ticketType);
    
    public int deleteTicketType(Integer typeId);
}
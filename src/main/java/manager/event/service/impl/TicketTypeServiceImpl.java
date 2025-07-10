package manager.event.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import manager.event.dao.TicketTypeDao;
import manager.event.service.TicketTypeService;
import manager.event.vo.EventTicketType;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {
    
    @Autowired
    private TicketTypeDao ticketTypeDao;
    
    @Override
    public List<EventTicketType> findTicketTypesByEventId(Integer eventId) {
        return ticketTypeDao.findByEventId(eventId);
    }
    
    @Override
    public EventTicketType findTicketTypeById(Integer typeId) {
        return ticketTypeDao.findById(typeId);
    }
    
    @Override
    @Transactional
    public int createTicketType(EventTicketType ticketType) {
        return ticketTypeDao.createTicketType(ticketType);
    }
    
    @Override
    @Transactional
    public int updateTicketType(EventTicketType ticketType) {
        return ticketTypeDao.updateTicketType(ticketType);
    }
    
    @Override
    @Transactional
    public int deleteTicketType(Integer typeId) {
        return ticketTypeDao.deleteTicketType(typeId);
    }
}
package manager.event.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.event.controller.TicketTypeController.EventInfo;
import manager.event.dao.TicketTypeDao;
import manager.event.service.TicketTypeService;
import manager.event.vo.EventTicketType;
import manager.event.vo.MngEventInfo;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {
    
    @Autowired
    private TicketTypeDao ticketTypeDao;
    
    @Override
    @Transactional
    public Integer createTicketType(EventTicketType ticketType) {
        return ticketTypeDao.createTicketType(ticketType);
    }
    
    @Override
    public EventTicketType findTicketTypeById(Integer typeId) {
        return ticketTypeDao.findTicketTypeById(typeId);
    }
    
    @Override
    public List<EventTicketType> findTicketTypesByEventId(Integer eventId) {
        return ticketTypeDao.findTicketTypesByEventId(eventId);
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

    @Override
    public EventInfo getEventInfo(Integer eventId) {
        return ticketTypeDao.getEventInfo(eventId);
    }
    
    
}
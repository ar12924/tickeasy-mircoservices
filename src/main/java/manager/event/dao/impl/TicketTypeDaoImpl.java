package manager.event.dao.impl;

import java.util.List;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import manager.event.dao.TicketTypeDao;
import manager.event.vo.EventTicketType;

@Repository
public class TicketTypeDaoImpl implements TicketTypeDao {
    
    @PersistenceContext
    private Session session;
    
    @Override
    public List<EventTicketType> findByEventId(Integer eventId) {
        try {
            String hql = "FROM MngEventTicketType WHERE eventId = :eventId ORDER BY typeId";
            return session.createQuery(hql, manager.event.vo.EventTicketType.class)
                    .setParameter("eventId", eventId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("查詢票種失敗: " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public EventTicketType findById(Integer typeId) {
        return session.get(EventTicketType.class, typeId);
    }
    
    @Override
    public int createTicketType(EventTicketType ticketType) {
        try {
            session.persist(ticketType);
            return 1;
        } catch (Exception e) {
            System.err.println("新增票種失敗: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public int updateTicketType(EventTicketType ticketType) {
        try {
            EventTicketType existing = session.get(EventTicketType.class, ticketType.getTypeId());
            if (existing == null) {
                return 0;
            }
            
            existing.setCategoryName(ticketType.getCategoryName());
            existing.setSellFromTime(ticketType.getSellFromTime());
            existing.setSellToTime(ticketType.getSellToTime());
            existing.setPrice(ticketType.getPrice());
            existing.setCapacity(ticketType.getCapacity());
            
            session.merge(existing);
            session.flush();
            return 1;
        } catch (Exception e) {
            System.err.println("更新票種失敗: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public int deleteTicketType(Integer typeId) {
        try {
            EventTicketType ticketType = session.get(EventTicketType.class, typeId);
            if (ticketType != null) {
                session.delete(ticketType);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            System.err.println("刪除票種失敗: " + e.getMessage());
            return 0;
        }
    }
}
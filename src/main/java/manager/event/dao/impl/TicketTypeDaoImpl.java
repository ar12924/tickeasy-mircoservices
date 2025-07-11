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
    public Integer createTicketType(EventTicketType ticketType) {
        try {
            System.out.println("=== DAO 層建立票種 ===");
            System.out.println("準備儲存票種: " + ticketType);
            
            session.persist(ticketType);
            session.flush(); // 確保立即執行並生成ID
            
            Integer typeId = ticketType.getTypeId();
            System.out.println("✅ 票種建立成功，生成ID: " + typeId);
            
            return typeId;
        } catch (Exception e) {
            System.err.println("❌ DAO 層建立票種失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public EventTicketType findTicketTypeById(Integer typeId) {
        try {
            return session.get(EventTicketType.class, typeId);
        } catch (Exception e) {
            System.err.println("查詢票種失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<EventTicketType> findTicketTypesByEventId(Integer eventId) {
        try {
            String hql = "FROM MngEventTicketType WHERE eventId = :eventId ORDER BY typeId ASC";
            return session.createQuery(hql,  manager.event.vo.EventTicketType.class)
                    .setParameter("eventId", eventId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("根據活動ID查詢票種失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public int updateTicketType(EventTicketType ticketType) {
        try {
        	EventTicketType existing = session.get(EventTicketType.class, ticketType.getTypeId());
            if (existing == null) {
                System.err.println("找不到票種ID: " + ticketType.getTypeId());
                return 0;
            }
            
            existing.setCategoryName(ticketType.getCategoryName());
            existing.setSellFromTime(ticketType.getSellFromTime());
            existing.setSellToTime(ticketType.getSellToTime());
            existing.setPrice(ticketType.getPrice());
            existing.setCapacity(ticketType.getCapacity());
            
            session.merge(existing);
            session.flush();
            
            System.out.println("✅ 票種更新成功，ID: " + ticketType.getTypeId());
            return 1;
        } catch (Exception e) {
            System.err.println("更新票種失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public int deleteTicketType(Integer typeId) {
        try {
        	EventTicketType ticketType = session.get(EventTicketType.class, typeId);
            if (ticketType != null) {
                session.delete(ticketType);
                session.flush();
                System.out.println("✅ 票種刪除成功，ID: " + typeId);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            System.err.println("刪除票種失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
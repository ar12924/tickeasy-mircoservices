package user.buy.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.EventInfoDAO;
import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;

/**
 * 活動資訊數據訪問實現類
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
@Repository
public class EventInfoDAOImpl implements EventInfoDAO {
	@PersistenceContext
	private Session session;
	
    @Override
    public EventBuyVO getEventInfoById(Integer eventId) {
//        Session session = getSession();
        return session.get(EventBuyVO.class, eventId);
    }
    
    @Override
    public List<EventBuyVO> getRecommendedEvents(int limit) {
//        Session session = getSession();
        String hql = "FROM EventBuyVO e " +
                "WHERE e.posted = 1 AND e.eventFromDate > CURRENT_TIMESTAMP " +
                "ORDER BY e.createTime DESC";
        return session.createQuery(hql, EventBuyVO.class)
                .setMaxResults(limit)
                .getResultList();
    }
    
    @Override
    public List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId) {
//        Session session = getSession();
        
        String hql = "FROM TicketTypeVO WHERE eventId = :eventId ORDER BY price ASC";
        
        List<TicketTypeVO> ticketTypes = session.createQuery(hql, TicketTypeVO.class)
                                               .setParameter("eventId", eventId)
                                               .getResultList();
        
        // 計算每個票券類型的剩餘票數
        for (TicketTypeVO ticketType : ticketTypes) {
            Integer remainingTickets = calculateRemainingTickets(ticketType.getTypeId());
            ticketType.setRemainingTickets(remainingTickets);
        }
        
        return ticketTypes;
    }
    
    @Override
    public Integer calculateRemainingTickets(Integer typeId) {
//        Session session = getSession();
        
        String sql = "SELECT ett.capacity - COALESCE(" +
                     "  (SELECT COUNT(*) FROM buyer_ticket bt " +
                     "   JOIN buyer_order bo ON bt.order_id = bo.order_id " +
                     "   WHERE bt.type_id = ett.type_id AND bo.is_paid = 1), 0" +
                     ") AS remaining_tickets " +
                     "FROM event_ticket_type ett " +
                     "WHERE ett.type_id = :typeId";
        
        Object result = session.createNativeQuery(sql)
                                        .setParameter("typeId", typeId)
                                        .uniqueResult();
        
        Integer remainingTickets = null;
        if (result != null) {
            remainingTickets = ((Number) result).intValue();
        }
        
        return remainingTickets != null ? remainingTickets : 0;
    }
    
    @Override
    public Integer checkFavoriteStatus(Integer memberId, Integer eventId) {
//        Session session = getSession();
        
        String hql = "SELECT f.followed FROM FavoriteVO f " +
                     "WHERE f.memberId = :memberId AND f.eventId = :eventId";
        
        return session.createQuery(hql, Integer.class)
                     .setParameter("memberId", memberId)
                     .setParameter("eventId", eventId)
                     .uniqueResult();
    }
    
    @Override
    public boolean insertFavorite(FavoriteVO favorite) {
//        Session session = getSession();
        try {
            session.persist(favorite);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateFavorite(FavoriteVO favorite) {
//        Session session = getSession();
        try {
            String hql = "UPDATE FavoriteVO SET followed = :followed " +
                         "WHERE memberId = :memberId AND eventId = :eventId";
            
            int rowsAffected = session.createQuery(hql)
                                     .setParameter("followed", favorite.getFollowed())
                                     .setParameter("memberId", favorite.getMemberId())
                                     .setParameter("eventId", favorite.getEventId())
                                     .executeUpdate();
            
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Integer calculateTotalRemainingTickets(Integer eventId) {
        List<TicketTypeVO> ticketTypes = getEventTicketTypesByEventId(eventId);
        
        int totalRemainingTickets = 0;
        for (TicketTypeVO ticketType : ticketTypes) {
            totalRemainingTickets += ticketType.getRemainingTickets();
        }
        
        return totalRemainingTickets;
    }
    
    @Override
    public byte[] getEventImage(Integer eventId) {
//        Session session = getSession();
        
        String hql = "SELECT e.image FROM EventBuyVO e WHERE e.eventId = :eventId";
        
        byte[] image = session.createQuery(hql, byte[].class)
                             .setParameter("eventId", eventId)
                             .uniqueResult();
        
        return image;
    }
}
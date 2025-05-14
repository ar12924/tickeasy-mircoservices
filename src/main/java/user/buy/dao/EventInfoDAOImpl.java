package user.buy.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;

/**
 * 活動資訊數據訪問實現類
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
public class EventInfoDAOImpl implements EventInfoDAO {
    
    @Override
    public EventBuyVO getEventInfoById(Integer eventId) {
        Session session = getSession();
        return session.get(EventBuyVO.class, eventId);
    }
    
    @Override
    public EventBuyVO getEventWithKeywords(Integer eventId) {
        Session session = getSession();
        
        String hql = "SELECT e, k.keywordName1, k.keywordName2, k.keywordName3 " +
                     "FROM EventBuyVO e LEFT JOIN KeywordCategoryVO k ON e.keywordId = k.keywordId " +
                     "WHERE e.eventId = :eventId";
        
        Object[] result = (Object[]) session.createQuery(hql)
                                           .setParameter("eventId", eventId)
                                           .uniqueResult();
        
        if (result != null) {
            EventBuyVO event = (EventBuyVO) result[0];
            event.setKeyword1((String) result[1]);
            event.setKeyword2((String) result[2]);
            event.setKeyword3((String) result[3]);
            return event;
        }
        
        return null;
    }
    
    @Override
    public List<EventBuyVO> getRecommendedEvents(int limit) {
        Session session = getSession();
        
        String hql = "SELECT e, k.keywordName1, k.keywordName2, k.keywordName3 " +
                     "FROM EventBuyVO e LEFT JOIN KeywordCategoryVO k ON e.keywordId = k.keywordId " +
                     "WHERE e.posted = 1 AND e.eventFromDate > CURRENT_TIMESTAMP " +
                     "ORDER BY e.createTime DESC";
        
        List<Object[]> results = session.createQuery(hql, Object[].class)
                                        .setMaxResults(limit)
                                        .getResultList();
        
        List<EventBuyVO> events = new ArrayList<>();
        for (Object[] result : results) {
            EventBuyVO event = (EventBuyVO) result[0];
            event.setKeyword1((String) result[1]);
            event.setKeyword2((String) result[2]);
            event.setKeyword3((String) result[3]);
            events.add(event);
        }
        
        return events;
    }
    
    @Override
    public List<EventBuyVO> searchEventsByKeyword(String keyword, int offset, int limit) {
        Session session = getSession();
        
        String hql = "SELECT e, k.keywordName1, k.keywordName2, k.keywordName3 " +
                     "FROM EventBuyVO e LEFT JOIN KeywordCategoryVO k ON e.keywordId = k.keywordId " +
                     "WHERE e.posted = 1 AND " +
                     "(e.eventName LIKE :keyword OR e.summary LIKE :keyword OR e.detail LIKE :keyword " +
                     "OR k.keywordName1 LIKE :keyword OR k.keywordName2 LIKE :keyword OR k.keywordName3 LIKE :keyword) " +
                     "ORDER BY e.eventFromDate ASC";
        
        String searchPattern = "%" + keyword + "%";
        
        List<Object[]> results = session.createQuery(hql, Object[].class)
                                        .setParameter("keyword", searchPattern)
                                        .setFirstResult(offset)
                                        .setMaxResults(limit)
                                        .getResultList();
        
        List<EventBuyVO> events = new ArrayList<>();
        for (Object[] result : results) {
            EventBuyVO event = (EventBuyVO) result[0];
            event.setKeyword1((String) result[1]);
            event.setKeyword2((String) result[2]);
            event.setKeyword3((String) result[3]);
            events.add(event);
        }
        
        return events;
    }
    
    @Override
    public List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId) {
        Session session = getSession();
        
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
        Session session = getSession();
        
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
        Session session = getSession();
        
        String hql = "SELECT f.followed FROM FavoriteVO f " +
                     "WHERE f.memberId = :memberId AND f.eventId = :eventId";
        
        return session.createQuery(hql, Integer.class)
                     .setParameter("memberId", memberId)
                     .setParameter("eventId", eventId)
                     .uniqueResult();
    }
    
    @Override
    public boolean insertFavorite(FavoriteVO favorite) {
        Session session = getSession();
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
        Session session = getSession();
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
}
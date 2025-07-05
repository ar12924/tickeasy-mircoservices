package user.ticket.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import user.ticket.dao.BuyerTicketDao;
import user.ticket.vo.BuyerTicketVO;
/**
 * 票券相關處理實作類
 * 創建者: archchang
 * 創建日期: 2025-06-20
 */
@Repository
public class BuyerTicketDaoImpl implements BuyerTicketDao {

    @PersistenceContext
    private Session session;

    @Override
    public boolean checkTicketOwnership(Integer ticketId, Integer memberId) {
        String hql = "SELECT COUNT(bt) FROM BuyerTicketVO bt WHERE bt.ticketId = :ticketId AND bt.currentHolderMemberId = :memberId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean ticketExists(Integer ticketId) {
        String hql = "SELECT COUNT(bt) FROM BuyerTicketVO bt WHERE bt.ticketId = :ticketId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Integer getTicketUsedStatus(Integer ticketId) {
        String hql = "SELECT bt.used FROM BuyerTicketVO bt WHERE bt.ticketId = :ticketId";
        List<Integer> results = session.createQuery(hql, Integer.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public boolean updateTicketOwner(Integer ticketId, Integer newOwnerId) {
        try {
            String hql = "UPDATE BuyerTicketVO bt SET bt.currentHolderMemberId = :newOwnerId, bt.updateTime = :updateTime WHERE bt.ticketId = :ticketId";
            int updatedRows = session.createQuery(hql)
                    .setParameter("newOwnerId", newOwnerId)
                    .setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
                    .setParameter("ticketId", ticketId)
                    .executeUpdate();
            return updatedRows > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public BuyerTicketVO getTicketById(Integer ticketId) {
        return session.get(BuyerTicketVO.class, ticketId);
    }

    @Override
    public List<BuyerTicketVO> getTicketsByMemberId(Integer memberId) {
        String hql = "FROM BuyerTicketVO bt WHERE bt.currentHolderMemberId = :memberId AND bt.used = 0 ORDER BY bt.ticketId DESC";
        return session.createQuery(hql, BuyerTicketVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
    
    @Override
    public Integer getTicketEventId(Integer ticketId) {
        // 通過票券查詢對應的訂單，再找到活動ID
        String hql = "SELECT ei.eventId FROM EventInfoVO ei, BuyerOrderVO bo, BuyerTicketVO bt " +
                     "WHERE bt.ticketId = :ticketId AND bt.orderId = bo.orderId AND bo.eventId = ei.eventId";
        List<Integer> results = session.createQuery(hql, Integer.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<BuyerTicketVO> getTicketsByMemberIdAndEventId(Integer memberId, Integer eventId) {
    	String sql = "SELECT bt.* FROM buyer_ticket bt " +
                "JOIN buyer_order bo ON bt.order_id = bo.order_id " +
                "WHERE bt.current_holder_member_id = ? " +
                "AND bo.event_id = ? " +
                "AND bt.is_used = 0 " +
                "ORDER BY bt.ticket_id DESC";
    
    	return session.createNativeQuery(sql, BuyerTicketVO.class)
            .setParameter(1, memberId)
            .setParameter(2, eventId)
            .getResultList();
    }
}
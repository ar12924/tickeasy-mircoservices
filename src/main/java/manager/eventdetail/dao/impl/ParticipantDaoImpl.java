package manager.eventdetail.dao.impl;

import manager.eventdetail.dao.ParticipantDao;
import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ParticipantDaoImpl implements ParticipantDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<BuyerTicketEventVer> getParticipantList(Integer eventId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                "LEFT JOIN FETCH bt.buyerOrder bo " +
                "LEFT JOIN FETCH bt.eventTicketType ett " +
                "WHERE ett.eventId = :eventId";
        return session
                .createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("eventId", eventId)
                .list();
    }

    @Override
    public BuyerTicketEventVer getParticipantDetail(Long ticketId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                "LEFT JOIN FETCH bt.buyerOrder " +
                "LEFT JOIN FETCH bt.eventTicketType " +
                "WHERE bt.ticketId = :ticketId";
        return session
                .createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("ticketId", ticketId)
                .uniqueResult();
    }

    @Override
    public BuyerOrderEventVer getOrderInfo(Integer orderId) {
        String hql = "FROM BuyerOrderEventVer bo " +
                "LEFT JOIN FETCH bo.buyerTicketEventVer " +
                "WHERE bo.orderId = :orderId";
        return session
                .createQuery(hql, BuyerOrderEventVer.class)
                .setParameter("orderId", orderId)
                .uniqueResult();
    }

    @Override
    public int countTicketsByOrderId(Integer orderId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE orderId = :orderId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("orderId", orderId)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams) {
        Map<String, Object> result = new HashMap<>();

        // --- 資料查詢 (Data Query) ---
        StringBuilder dataHqlBuilder = new StringBuilder(
            "FROM BuyerTicketEventVer bt " +
            "LEFT JOIN FETCH bt.buyerOrder bo " +
            "LEFT JOIN FETCH bt.eventTicketType ett " +
            "WHERE ett.eventId = :eventId ");
        Map<String, Object> dataParams = new HashMap<>();
        dataParams.put("eventId", eventId);

        if (searchParams.containsKey("participantName")) {
            dataHqlBuilder.append("AND bt.participantName LIKE :participantName ");
            dataParams.put("participantName", "%" + searchParams.get("participantName") + "%");
        }
        if (searchParams.containsKey("email")) {
            dataHqlBuilder.append("AND bt.email LIKE :email ");
            dataParams.put("email", "%" + searchParams.get("email") + "%");
        }
        if (searchParams.containsKey("phone")) {
            dataHqlBuilder.append("AND bt.phone LIKE :phone ");
            dataParams.put("phone", "%" + searchParams.get("phone") + "%");
        }
        if (searchParams.containsKey("status")) {
            dataHqlBuilder.append("AND bt.status = :status ");
            dataParams.put("status", searchParams.get("status"));
        }
        if (searchParams.containsKey("ticketTypeId")) {
            dataHqlBuilder.append("AND bt.typeId = :typeId ");
            dataParams.put("typeId", searchParams.get("ticketTypeId"));
        }
        if (searchParams.containsKey("isUsed")) {
            dataHqlBuilder.append("AND bt.isUsed = :isUsed ");
            dataParams.put("isUsed", searchParams.get("isUsed"));
        }
        
        Query<BuyerTicketEventVer> dataQuery = session.createQuery(dataHqlBuilder.toString(), BuyerTicketEventVer.class);
        dataParams.forEach(dataQuery::setParameter);
        
        int pageNumber = (int) searchParams.getOrDefault("pageNumber", 1);
        int pageSize = (int) searchParams.getOrDefault("pageSize", 10);
        int firstResult = (pageNumber - 1) * pageSize;
        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(pageSize);
        List<BuyerTicketEventVer> list = dataQuery.list();

        // --- 總數查詢 (Count Query) ---
        StringBuilder countHqlBuilder = new StringBuilder(
            "SELECT COUNT(bt.ticketId) " +
            "FROM BuyerTicketEventVer bt " +
            "JOIN bt.eventTicketType ett " +
            "WHERE ett.eventId = :eventId ");
        Map<String, Object> countParams = new HashMap<>();
        countParams.put("eventId", eventId);

        if (searchParams.containsKey("participantName")) {
            countHqlBuilder.append("AND bt.participantName LIKE :participantName ");
            countParams.put("participantName", "%" + searchParams.get("participantName") + "%");
        }
        if (searchParams.containsKey("email")) {
            countHqlBuilder.append("AND bt.email LIKE :email ");
            countParams.put("email", "%" + searchParams.get("email") + "%");
        }
        if (searchParams.containsKey("phone")) {
            countHqlBuilder.append("AND bt.phone LIKE :phone ");
            countParams.put("phone", "%" + searchParams.get("phone") + "%");
        }
        if (searchParams.containsKey("status")) {
            countHqlBuilder.append("AND bt.status = :status ");
            countParams.put("status", searchParams.get("status"));
        }
        if (searchParams.containsKey("ticketTypeId")) {
            countHqlBuilder.append("AND bt.typeId = :typeId ");
            countParams.put("typeId", searchParams.get("ticketTypeId"));
        }
        if (searchParams.containsKey("isUsed")) {
            countHqlBuilder.append("AND bt.isUsed = :isUsed ");
            countParams.put("isUsed", searchParams.get("isUsed"));
        }

        Query<Long> countQuery = session.createQuery(countHqlBuilder.toString(), Long.class);
        countParams.forEach(countQuery::setParameter);
        Long total = countQuery.uniqueResult();

        result.put("total", total);
        result.put("data", list);
        return result;
    }

    @Override
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        String hql = "FROM EventTicketType ett WHERE ett.eventId = :eventId";
        return session.createQuery(hql, EventTicketType.class)
                .setParameter("eventId", eventId)
                .list();
    }

    @Override
    public boolean updateTicketStatus(Long ticketId, Integer status, Integer isUsed) {
        String hql = "UPDATE BuyerTicketEventVer SET status = :status, isUsed = :isUsed WHERE ticketId = :ticketId";
        int updated = session.createQuery(hql)
                .setParameter("status", status)
                .setParameter("isUsed", isUsed)
                .setParameter("ticketId", ticketId)
                .executeUpdate();
        return updated > 0;
    }
}
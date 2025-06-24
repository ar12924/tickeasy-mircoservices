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
    public BuyerTicketEventVer getParticipantDetail(Integer ticketId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                     "LEFT JOIN FETCH bt.buyerOrder " +
                     "LEFT JOIN FETCH bt.eventTicketType " +
                     "WHERE bt.ticketId = :ticketId";
        return session.createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("ticketId", ticketId)
                .uniqueResult();
    }

    @Override
    public BuyerOrderEventVer getOrderInfo(Integer orderId) {
        String hql = "FROM BuyerOrderEventVer bo WHERE bo.orderId = :orderId";
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

        // 步驟 1: 根據 eventId 獲取 eventName
        String eventName = getEventNameById(eventId);
        if (eventName == null) {
            System.out.println("DAO: 找不到 eventId " + eventId + " 對應的 eventName。");
            result.put("total", 0L);
            result.put("participants", new java.util.ArrayList<>());
            return result;
        }

        // --- 統一的 HQL WHERE 條件和參數 ---
        StringBuilder whereClause = new StringBuilder("WHERE bt.eventName = :eventName ");
        Map<String, Object> params = new HashMap<>();
        params.put("eventName", eventName);

        if (searchParams.containsKey("participantName") && searchParams.get("participantName") != null && !searchParams.get("participantName").toString().isEmpty()) {
            whereClause.append("AND bt.participantName LIKE :participantName ");
            params.put("participantName", "%" + searchParams.get("participantName") + "%");
        }
        if (searchParams.containsKey("email") && searchParams.get("email") != null && !searchParams.get("email").toString().isEmpty()) {
            whereClause.append("AND bt.email LIKE :email ");
            params.put("email", "%" + searchParams.get("email") + "%");
        }
        if (searchParams.containsKey("phone") && searchParams.get("phone") != null && !searchParams.get("phone").toString().isEmpty()) {
            whereClause.append("AND bt.phone LIKE :phone ");
            params.put("phone", "%" + searchParams.get("phone") + "%");
        }
        if (searchParams.containsKey("status") && searchParams.get("status") != null) {
            whereClause.append("AND bt.status = :status ");
            params.put("status", searchParams.get("status"));
        }
        if (searchParams.containsKey("ticketTypeId") && searchParams.get("ticketTypeId") != null) {
            whereClause.append("AND ett.typeId = :typeId ");
            params.put("typeId", searchParams.get("ticketTypeId"));
        }
        if (searchParams.containsKey("isUsed") && searchParams.get("isUsed") != null) {
            whereClause.append("AND bt.isUsed = :isUsed ");
            params.put("isUsed", searchParams.get("isUsed"));
        }

        // --- 資料查詢 (Data Query) ---
        String dataHql = "SELECT bt FROM BuyerTicketEventVer bt " +
                         "LEFT JOIN FETCH bt.buyerOrder bo " +
                         "LEFT JOIN FETCH bt.eventTicketType ett " +
                         whereClause.toString();

        System.out.println("DAO: 執行資料查詢 HQL: " + dataHql);
        System.out.println("DAO: 參數: " + params);
        
        Query<BuyerTicketEventVer> dataQuery = session.createQuery(dataHql, BuyerTicketEventVer.class);
        params.forEach(dataQuery::setParameter);
        
        int pageNumber = (int) searchParams.getOrDefault("pageNumber", 1);
        int pageSize = (int) searchParams.getOrDefault("pageSize", 10);
        int firstResult = (pageNumber - 1) * pageSize;
        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(pageSize);
        List<BuyerTicketEventVer> list = dataQuery.list();

        if (list.isEmpty()) {
            System.out.println("DAO: 對於 eventName '" + eventName + "' (eventId: " + eventId + ") 找不到任何參與者資料。");
        }

        // --- 總數查詢 (Count Query) ---
        String countHql = "SELECT COUNT(bt.ticketId) FROM BuyerTicketEventVer bt " +
                          "LEFT JOIN bt.eventTicketType ett " +
                          whereClause.toString();

        System.out.println("DAO: 執行總數查詢 HQL: " + countHql);
        System.out.println("DAO: 參數: " + params);

        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        params.forEach(countQuery::setParameter);
        Long total = countQuery.uniqueResult();

        System.out.println("DAO: 總數查詢結果: " + total);

        result.put("total", total);
        result.put("participants", list);
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
    public boolean updateTicketStatus(Integer ticketId, Integer status, Integer isUsed) {
        String hql = "UPDATE BuyerTicketEventVer SET status = :status, isUsed = :isUsed WHERE ticketId = :ticketId";
        int updated = session.createQuery(hql)
                .setParameter("status", status)
                .setParameter("isUsed", isUsed)
                .setParameter("ticketId", ticketId)
                .executeUpdate();
        return updated > 0;
    }

    @Override
    public String getEventNameById(Integer eventId) {
        String hql = "SELECT e.eventName FROM EventInfoEventVer e WHERE e.eventId = :eventId";
        return session.createQuery(hql, String.class)
                .setParameter("eventId", eventId)
                .uniqueResult();
    }

    @Override
    public Map<String, Object> getParticipants(Integer eventId, int page, int pageSize) {
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("pageNumber", page);
        searchParams.put("pageSize", pageSize);
        return searchParticipants(eventId, searchParams);
    }

    @Override
    public List<EventTicketType> getTicketTypesByEventId(Integer eventId) {
        return getEventTicketTypes(eventId);
    }
}
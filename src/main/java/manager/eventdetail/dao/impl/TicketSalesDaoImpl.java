package manager.eventdetail.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.vo.EventTicketType;

@Repository
public class TicketSalesDaoImpl implements TicketSalesDao {

    @Override
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        String hql = "FROM EventTicketType WHERE eventId = :eventId ORDER BY typeId";
        return getSession().createQuery(hql, EventTicketType.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    @Override
    public Integer getSoldTicketCount(Integer typeId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE typeId = :typeId";
        return ((Long) getSession().createQuery(hql)
                .setParameter("typeId", typeId)
                .uniqueResult()).intValue();
    }

    @Override
    public Integer getUsedTicketCount(Integer typeId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE typeId = :typeId AND isUsed = 1";
        return ((Long) getSession().createQuery(hql)
                .setParameter("typeId", typeId)
                .uniqueResult()).intValue();
    }

    @Override
    public Map<Integer, Map<String, Object>> getTicketSalesStatistics(Integer eventId) {
        Map<Integer, Map<String, Object>> result = new HashMap<>();
        // 獲取所有票種
        List<EventTicketType> ticketTypes = getEventTicketTypes(eventId);
        
        for (EventTicketType ticketType : ticketTypes) {
            Integer typeId = ticketType.getTypeId();
            Integer capacity = ticketType.getCapacity();
            Integer soldCount = getSoldTicketCount(typeId);
            Integer usedCount = getUsedTicketCount(typeId);
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("typeId", typeId);
            statistics.put("categoryName", ticketType.getCategoryName());
            statistics.put("capacity", capacity);
            statistics.put("soldCount", soldCount);
            statistics.put("usedCount", usedCount);
            statistics.put("remainingCount", capacity - soldCount);
            statistics.put("soldPercentage", capacity > 0 ? (double) soldCount / capacity * 100 : 0);
            
            result.put(typeId, statistics);
        }
        
        return result;
    }
    

}

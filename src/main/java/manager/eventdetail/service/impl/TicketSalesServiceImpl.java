package manager.eventdetail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.service.TicketSalesService;
import manager.eventdetail.vo.EventTicketType;


@Service
public class TicketSalesServiceImpl implements TicketSalesService {
    
    @Autowired
    private TicketSalesDao ticketSalesDao;
    
    @Transactional
    @Override
    public Map<String, Object> getTicketSalesStatus(Integer eventId) {
        Map<String, Object> result = new HashMap<>();
        
        // 獲取所有票種
        List<EventTicketType> ticketTypes = ticketSalesDao.getEventTicketTypes(eventId);
        result.put("ticketTypes", ticketTypes);
        
        // 獲取銷售統計數據
        Map<Integer, Map<String, Object>> salesStatistics = ticketSalesDao.getTicketSalesStatistics(eventId);
        result.put("salesStatistics", salesStatistics);
        
        // 計算總體銷售情況
        int totalCapacity = 0;
        int totalSold = 0;
        int totalUsed = 0;
        
        for (Map<String, Object> statistics : salesStatistics.values()) {
            totalCapacity += (Integer) statistics.get("capacity");
            totalSold += (Integer) statistics.get("soldCount");
            totalUsed += (Integer) statistics.get("usedCount");
        }
        
        Map<String, Object> totalStatistics = new HashMap<>();
        totalStatistics.put("totalCapacity", totalCapacity);
        totalStatistics.put("totalSold", totalSold);
        totalStatistics.put("totalRemaining", totalCapacity - totalSold);
        totalStatistics.put("totalUsed", totalUsed);
        totalStatistics.put("totalSoldPercentage", totalCapacity > 0 ? (double) totalSold / totalCapacity * 100 : 0);
        
        result.put("totalStatistics", totalStatistics);
        
        return result;
    }
    
    @Transactional
    @Override
    public Map<String, Object> getTicketTypeDetail(Integer typeId) {
        Map<String, Object> result = new HashMap<>();
        
        // 獲取票種信息
        EventTicketType ticketType = null;
        List<EventTicketType> ticketTypes = ticketSalesDao.getEventTicketTypes(null);
        for (EventTicketType type : ticketTypes) {
            if (type.getTypeId().equals(typeId)) {
                ticketType = type;
                break;
            }
        }
        
        if (ticketType == null) {
            return result;
        }
        
        result.put("ticketType", ticketType);
        
        // 獲取銷售統計數據
        Integer soldCount = ticketSalesDao.getSoldTicketCount(typeId);
        Integer usedCount = ticketSalesDao.getUsedTicketCount(typeId);
        Integer capacity = ticketType.getCapacity();
        
        result.put("soldCount", soldCount);
        result.put("usedCount", usedCount);
        result.put("remainingCount", capacity - soldCount);
        result.put("soldPercentage", capacity > 0 ? (double) soldCount / capacity * 100 : 0);
        
        return result;
    }
}
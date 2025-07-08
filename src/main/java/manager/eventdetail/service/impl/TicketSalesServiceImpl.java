package manager.eventdetail.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.service.TicketSalesService;
import manager.eventdetail.service.EventInfoVOService;
import manager.eventdetail.vo.EventTicketType;
import manager.eventdetail.vo.EventInfoEventVer;

@Service
public class TicketSalesServiceImpl implements TicketSalesService {
    
    @Autowired
    private TicketSalesDao ticketSalesDao;
    
    @Autowired
    private EventInfoVOService eventInfoVOService;
    
    @Transactional
    @Override
    public Map<String, Object> getTicketSalesStatus(Integer eventId) {
        Map<String, Object> result = new HashMap<>();
        
        // 獲取活動信息
        EventInfoEventVer event = eventInfoVOService.getEventById(eventId);
        if (event == null) {
            return result;
        }
        
        result.put("eventName", event.getEventName());
        Integer capacity = event.getCapacity();
        result.put("capacity", capacity);
        
        // 獲取所有票種
        List<EventTicketType> ticketTypes = ticketSalesDao.getEventTicketTypes(eventId);
        
        // 準備銷售數據
        List<Map<String, Object>> salesData = new ArrayList<>();
        double totalRevenue = 0.0;
        for (EventTicketType ticketType : ticketTypes) {
            Map<String, Object> salesItem = new HashMap<>();
            salesItem.put("categoryName", ticketType.getCategoryName());
            salesItem.put("capacity", ticketType.getCapacity());
            Integer soldCount = ticketSalesDao.getSoldTicketCount(ticketType.getTypeId());
            soldCount = soldCount != null ? soldCount : 0;
            int unsold = ticketType.getCapacity() != null ? (ticketType.getCapacity() - soldCount) : 0;
            salesItem.put("ticketsSold", soldCount);
            salesItem.put("unsold", unsold);
            double revenue = soldCount * ticketType.getPrice().doubleValue();
            salesItem.put("totalRevenue", revenue);
            salesData.add(salesItem);
            totalRevenue += revenue;
        }
        result.put("salesData", salesData);
        result.put("totalRevenue", totalRevenue);

        // 正確計算已售出、未銷售、銷售率
        Integer soldCount = ticketSalesDao.getSoldTicketCountByEventId(eventId);
        int unsold = capacity != null && soldCount != null ? (capacity - soldCount) : 0;
        double salesRate = (capacity != null && capacity > 0 && soldCount != null) ? (soldCount * 100.0 / capacity) : 0;
        result.put("soldCount", soldCount);
        result.put("unsold", unsold);
        result.put("salesRate", salesRate);
        
        // 添加圓餅圖數據
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (EventTicketType ticketType : ticketTypes) {
            Integer typeSoldCount = ticketSalesDao.getSoldTicketCount(ticketType.getTypeId());
            typeSoldCount = typeSoldCount != null ? typeSoldCount : 0;
            if (typeSoldCount > 0) {  // 只顯示有銷售的票種
                Map<String, Object> chartItem = new HashMap<>();
                chartItem.put("name", ticketType.getCategoryName());
                chartItem.put("value", typeSoldCount);
                chartItem.put("revenue", typeSoldCount * ticketType.getPrice().doubleValue());
                chartData.add(chartItem);
            }
        }
        result.put("chartData", chartData);
        
        return result;
    }
}
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
        
        // 加入銷售趨勢資料
        List<Map<String, Object>> trendData = ticketSalesDao.getSalesTrend(eventId);
        result.put("trendData", trendData);

        return result;
    }

    @Override
    public Map<String, Object> getTicketTypeTrendData(Integer eventId) {
        List<Object[]> raw = ticketSalesDao.findTicketTypeTrendByEventId(eventId);
        // 1. 收集所有日期、票種
        java.util.Set<String> dateSet = new java.util.TreeSet<>();
        java.util.Set<String> typeSet = new java.util.LinkedHashSet<>();
        Map<String, Map<String, Long>> dataMap = new HashMap<>();
        Map<String, List<Map<String, Object>>> trendTypeDetail = new HashMap<>();
        for (Object[] row : raw) {
            String date = row[0].toString();
            String type = (String) row[1];
            Long count = (Long) row[2];
            dateSet.add(date);
            typeSet.add(type);
            dataMap.computeIfAbsent(type, k -> new HashMap<>()).put(date, count);
            // trendTypeDetail
            trendTypeDetail.computeIfAbsent(date, k -> new java.util.ArrayList<>())
                .add(new HashMap<String, Object>() {{
                    put("type", type);
                    put("count", count);
                }});
        }
        // 2. 組裝多票種
        List<String> categories = new ArrayList<>(dateSet);
        List<Map<String, Object>> series = new ArrayList<>();
        for (String type : typeSet) {
            List<Long> counts = new ArrayList<>();
            for (String date : categories) {
                counts.add(dataMap.getOrDefault(type, java.util.Collections.emptyMap()).getOrDefault(date, 0L));
            }
            Map<String, Object> serie = new HashMap<>();
            serie.put("name", type);
            serie.put("data", counts);
            series.add(serie);
        }
        // 3. 合併計算每日加總
        List<Map<String, Object>> trendData = new ArrayList<>();
        for (String date : categories) {
            long sum = 0;
            for (String type : typeSet) {
                sum += dataMap.getOrDefault(type, java.util.Collections.emptyMap()).getOrDefault(date, 0L);
            }
            Map<String, Object> item = new HashMap<>();
            item.put("date", date);
            item.put("value", sum);
            trendData.add(item);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories);
        result.put("series", series); // 多票種序列
        result.put("trendData", trendData); // 單線加總
        result.put("trendTypeDetail", trendTypeDetail); // 日期對應票種明細
        return result;
    }
}
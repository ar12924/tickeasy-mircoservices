package user.buy.service;
import user.buy.dao.EventInfoDAO;
import user.buy.dao.EventInfoDAOImpl;

import java.util.List;
import java.util.Map;

/**
 * 活動資訊服務實現類
 */
public class EventInfoServiceImpl implements EventInfoService{
private EventInfoDAO eventInfoDAO;
    
    public EventInfoServiceImpl() {
        this.eventInfoDAO = new EventInfoDAOImpl();
    }

    @Override
    public Map<String, Object> getEventDetail(Integer eventId, Integer memberId) {
        // 獲取活動基本信息
        Map<String, Object> eventInfo = eventInfoDAO.getEventInfoById(eventId);
        
        if (eventInfo != null) {
            // 計算活動的剩餘票券數量
            List<Map<String, Object>> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId(eventId);
            int totalRemainingTickets = 0;
            
            for (Map<String, Object> ticketType : ticketTypes) {
                totalRemainingTickets += (Integer) ticketType.get("remainingTickets");
            }
            
            eventInfo.put("remainingTickets", totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                // 檢查會員是否已關注此活動的邏輯
                // 此處簡化處理，可以通過查詢favorite表實現
                eventInfo.put("isFollowed", 0); // 預設未關注
            }
        }
        
        return eventInfo;
    }

    @Override
    public List<Map<String, Object>> getRecommendedEvents(int limit, Integer memberId) {
        // 獲取推薦活動列表
        List<Map<String, Object>> events = eventInfoDAO.getRecommendedEvents(limit);
        
        // 計算每個活動的剩餘票券數量
        for (Map<String, Object> event : events) {
            // 獲取票券類型
            List<Map<String, Object>> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId((Integer) event.get("eventId"));
            int totalRemainingTickets = 0;
            
            for (Map<String, Object> ticketType : ticketTypes) {
                totalRemainingTickets += (Integer) ticketType.get("remainingTickets");
            }
            
            event.put("remainingTickets", totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                // 實際應該在DAO層實現查詢會員關注狀態的方法
                event.put("isFollowed", 0); // 預設未關注
            }
        }
        
        return events;
    }

    @Override
    public List<Map<String, Object>> searchEvents(String keyword, int page, int pageSize, Integer memberId) {
        // 計算偏移量
        int offset = (page - 1) * pageSize;
        
        // 搜索活動
        List<Map<String, Object>> events = eventInfoDAO.searchEventsByKeyword(keyword, offset, pageSize);
        
        // 計算每個活動的剩餘票券數量
        for (Map<String, Object> event : events) {
            // 獲取票券類型
            List<Map<String, Object>> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId((Integer) event.get("eventId"));
            int totalRemainingTickets = 0;
            
            for (Map<String, Object> ticketType : ticketTypes) {
                totalRemainingTickets += (Integer) ticketType.get("remainingTickets");
            }
            
            event.put("remainingTickets", totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                // 實際應該在DAO層實現查詢會員關注狀態的方法
                event.put("isFollowed", 0); // 預設未關注
            }
        }
        
        return events;
    }

    @Override
    public List<Map<String, Object>> getEventTicketTypes(Integer eventId) {
        return eventInfoDAO.getEventTicketTypesByEventId(eventId);
    }

    @Override
    public boolean checkTicketAvailability(Integer typeId, Integer quantity) {
        return eventInfoDAO.hasEnoughRemainingTickets(typeId, quantity);
    }

    @Override
    public boolean toggleEventFavorite(Integer memberId, Integer eventId, Integer isFollowed) {
        return eventInfoDAO.setEventFavoriteStatus(memberId, eventId, isFollowed);
    }
}

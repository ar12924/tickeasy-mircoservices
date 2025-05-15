package user.buy.service.impl;

import user.buy.dao.EventInfoDAO;
import user.buy.dao.impl.EventInfoDAOImpl;
import user.buy.service.EventInfoService;
import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;

import java.util.List;

/**
 * 活動資訊服務實現類
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
public class EventInfoServiceImpl implements EventInfoService {
    private EventInfoDAO eventInfoDAO;
    
    public EventInfoServiceImpl() {
        this.eventInfoDAO = new EventInfoDAOImpl();
    }

    @Override
    public EventBuyVO getEventDetail(Integer eventId, Integer memberId) {
    	// 獲取活動基本信息和關鍵字
        EventBuyVO eventVO = eventInfoDAO.getEventWithKeywords(eventId);
        
        if (eventVO != null) {
            // 獲取票券類型列表
            List<TicketTypeVO> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId(eventId);
            
            // 計算活動的剩餘票券數量
            int totalRemainingTickets = 0;
            for (TicketTypeVO ticketType : ticketTypes) {
                totalRemainingTickets += ticketType.getRemainingTickets();
            }
            
            eventVO.setRemainingTickets(totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                Integer favoriteStatus = eventInfoDAO.checkFavoriteStatus(memberId, eventId);
                eventVO.setFollowed(favoriteStatus != null ? favoriteStatus : 0);
            } else {
                eventVO.setFollowed(0); // 預設未關注
            }
        }
        
        return eventVO;
    }

    @Override
    public List<EventBuyVO> getRecommendedEvents(int limit, Integer memberId) {
        // 獲取推薦活動列表
        List<EventBuyVO> events = eventInfoDAO.getRecommendedEvents(limit);
        
        // 計算每個活動的剩餘票券數量，設置關注狀態
        for (EventBuyVO event : events) {
            // 獲取票券類型
            List<TicketTypeVO> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId(event.getEventId());
            
            // 計算總剩餘票數
            int totalRemainingTickets = 0;
            for (TicketTypeVO ticketType : ticketTypes) {
                totalRemainingTickets += ticketType.getRemainingTickets();
            }
            
            event.setRemainingTickets(totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                Integer favoriteStatus = eventInfoDAO.checkFavoriteStatus(memberId, event.getEventId());
                event.setFollowed(favoriteStatus != null ? favoriteStatus : 0);
            } else {
                event.setFollowed(0); // 預設未關注
            }
        }
        
        return events;
    }

    @Override
    public List<EventBuyVO> searchEvents(String keyword, int page, int pageSize, Integer memberId) {
        // 計算偏移量
        int offset = (page - 1) * pageSize;
        
        // 搜索活動
        List<EventBuyVO> events = eventInfoDAO.searchEventsByKeyword(keyword, offset, pageSize);
        
        // 計算每個活動的剩餘票券數量，設置關注狀態
        for (EventBuyVO  event : events) {
            // 獲取票券類型
            List<TicketTypeVO> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId(event.getEventId());
            
            // 計算總剩餘票數
            int totalRemainingTickets = 0;
            for (TicketTypeVO ticketType : ticketTypes) {
                totalRemainingTickets += ticketType.getRemainingTickets();
            }
            
            event.setRemainingTickets(totalRemainingTickets);
            
            // 如果有登入會員，檢查是否已關注該活動
            if (memberId != null) {
                Integer favoriteStatus = eventInfoDAO.checkFavoriteStatus(memberId, event.getEventId());
                event.setFollowed(favoriteStatus != null ? favoriteStatus : 0);
            } else {
                event.setFollowed(0); // 預設未關注
            }
        }
        
        return events;
    }

    @Override
    public List<TicketTypeVO> getEventTicketTypes(Integer eventId) {
        return eventInfoDAO.getEventTicketTypesByEventId(eventId);
    }

    @Override
    public boolean checkTicketAvailability(Integer typeId, Integer quantity) {
    	Integer remainingTickets = eventInfoDAO.calculateRemainingTickets(typeId);
        return remainingTickets >= quantity;
    }

    @Override
    public boolean toggleEventFavorite(Integer memberId, Integer eventId, Integer isFollowed) {
        // 檢查是否已存在關注記錄
        Integer currentStatus = eventInfoDAO.checkFavoriteStatus(memberId, eventId);
        
     // 使用 try-catch 來處理可能的異常，因為這涉及數據庫寫操作
        try {
            if (currentStatus == null) {
                // 新增關注記錄
                FavoriteVO favorite = new FavoriteVO();
                favorite.setMemberId(memberId);
                favorite.setEventId(eventId);
                favorite.setFollowed(isFollowed);
                
                return eventInfoDAO.insertFavorite(favorite);
            } else {
                // 更新現有記錄
                FavoriteVO favorite = new FavoriteVO();
                favorite.setMemberId(memberId);
                favorite.setEventId(eventId);
                favorite.setFollowed(isFollowed);
                
                return eventInfoDAO.updateFavorite(favorite);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public byte[] getEventImage(Integer eventId) {
    	EventBuyVO event = eventInfoDAO.getEventInfoById(eventId);
        return event != null ? event.getImage() : null;
    }
}
package user.buy.service.impl;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.buy.dao.EventInfoDAO;
import user.buy.service.EventInfoService;
import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;

/**
 * 活動資訊服務實現類 創建者: archchang 創建日期: 2025-05-07
 */
@Service
@Transactional
public class EventInfoServiceImpl implements EventInfoService {
	@Autowired
	private EventInfoDAO eventInfoDAO;

	@Override
	public EventBuyVO getEventDetail(Integer eventId, Integer memberId) {
		EventBuyVO eventVO = eventInfoDAO.getEventInfoById(eventId);

		if (eventVO == null) {
			return null;
		}

		// 計算剩餘票券數量
		Integer totalRemainingTickets = eventInfoDAO.calculateTotalRemainingTickets(eventId);
		eventVO.setRemainingTickets(totalRemainingTickets);

		// 設置關注狀態
		Integer favoriteStatus = null;
		if (memberId != null) {
			favoriteStatus = eventInfoDAO.checkFavoriteStatus(memberId, eventId);
		}
		eventVO.setFollowed(favoriteStatus != null ? favoriteStatus : 0);

		return eventVO;
	}

	@Override
	public List<EventBuyVO> getRecommendedEvents(int limit, Integer memberId) {
		// 獲取推薦活動列表
		List<EventBuyVO> events = eventInfoDAO.getRecommendedEvents(limit);

		// 計算每個活動的剩餘票券數量，設置關注狀態
		for (EventBuyVO event : events) {
			// 計算總剩餘票數
			Integer totalRemainingTickets = eventInfoDAO.calculateTotalRemainingTickets(event.getEventId());
			event.setRemainingTickets(totalRemainingTickets);
			Integer favoriteStatus = null;
			if (memberId != null) {
				favoriteStatus = eventInfoDAO.checkFavoriteStatus(memberId, event.getEventId());
			}
			event.setFollowed(favoriteStatus != null ? favoriteStatus : 0);
		}

		return events;
	}

	@Override
	public List<TicketTypeVO> getEventTicketTypes(Integer eventId) {
		List<TicketTypeVO> ticketTypes = eventInfoDAO.getEventTicketTypesByEventId(eventId);
		
		// 業務邏輯：為每個票券類型計算剩餘票數
        for (TicketTypeVO ticketType : ticketTypes) {
            Integer remainingTickets = eventInfoDAO.calculateRemainingTickets(ticketType.getTypeId());
            ticketType.setRemainingTickets(remainingTickets != null ? Math.max(0, remainingTickets) : 0);
        }

        return ticketTypes;
	}

	@Override
	public boolean checkTicketAvailability(Integer typeId, Integer quantity) {
		// 業務邏輯：參數驗證
        if (typeId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
		Integer remainingTickets = eventInfoDAO.calculateRemainingTickets(typeId);
		
		// 業務邏輯：庫存檢查
		return remainingTickets != null && remainingTickets >= quantity;
	}

	@Override
	public boolean toggleEventFavorite(Integer memberId, Integer eventId, Integer isFollowed) {
		// 業務邏輯：檢查現有狀態並決定操作
		Integer currentStatus = eventInfoDAO.checkFavoriteStatus(memberId, eventId);

		FavoriteVO favorite = new FavoriteVO();
		favorite.setMemberId(memberId);
		favorite.setEventId(eventId);
		favorite.setFollowed(isFollowed);

		if (currentStatus == null) {
			return eventInfoDAO.insertFavorite(favorite);
		} else {
			return eventInfoDAO.updateFavorite(favorite);
		}
	}

	@Override
	public byte[] getEventImage(Integer eventId) {

		return eventInfoDAO.getEventImage(eventId);
	}

	// 新增：Stream 方式獲取圖片
	@Override
	public InputStream getEventImageStream(Integer eventId) {
		return eventInfoDAO.getEventImageStream(eventId);
	}

	@Override
	public Integer calculateTotalRemainingTickets(Integer eventId) {
		return eventInfoDAO.calculateTotalRemainingTickets(eventId);
	}
}
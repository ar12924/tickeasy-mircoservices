package user.buy.service;

import java.io.InputStream;
import java.util.List;

import user.buy.vo.EventBuyVO;
import user.buy.vo.TicketTypeVO;

/**
 * 活動資訊服務接口
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
public interface EventInfoService {
	/**
     * 根據活動ID獲取活動詳情
     * 
     * @param eventId 活動ID
     * @param memberId 當前會員ID（可為null）
     * @return 活動詳情
     */
	EventBuyVO getEventDetail(Integer eventId, Integer memberId);
    
    /**
     * 獲取推薦活動列表
     * 
     * @param limit 限制數量
     * @param memberId 當前會員ID（可為null）
     * @return 活動列表
     */
	List<EventBuyVO> getRecommendedEvents(int limit, Integer memberId);    
    
    /**
     * 獲取活動的票券類型列表
     * 
     * @param eventId 活動ID
     * @return 票券類型列表
     */
	List<TicketTypeVO> getEventTicketTypes(Integer eventId);
    
    /**
     * 檢查票券是否有足夠庫存
     * 
     * @param typeId 票券類型ID
     * @param quantity 需要的數量
     * @return 是否有足夠庫存
     */
    boolean checkTicketAvailability(Integer typeId, Integer quantity);
    
    /**
     * 設置用戶對活動的關注狀態
     * 
     * @param memberId 會員ID
     * @param eventId 活動ID
     * @param isFollowed 是否關注（1：關注，0：取消關注）
     * @return 是否設置成功
     */
    boolean toggleEventFavorite(Integer memberId, Integer eventId, Integer isFollowed);
    
    /**
     * 獲取活動圖片
     * 
     * @param eventId 活動ID
     * @return 圖片數據
     */
    byte[] getEventImage(Integer eventId);
    
    InputStream getEventImageStream(Integer eventId);
    
    /**
     * 計算活動的總剩餘票數
     *
     * @param eventId 活動ID
     * @return 總剩餘票數
     */
    Integer calculateTotalRemainingTickets(Integer eventId);
}

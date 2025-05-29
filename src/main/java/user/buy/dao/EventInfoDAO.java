package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;
/**
 * 活動資訊數據訪問接口
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
public interface EventInfoDAO extends CommonDao {
    /**
     * 根據活動ID獲取活動資訊
     * 
     * @param eventId 活動ID
     * @return 活動資訊VO
     */
    EventBuyVO getEventInfoById(Integer eventId);
    
    /**
     * 獲取推薦活動列表
     * 
     * @param limit 限制數量
     * @return 活動資訊VO列表
     */
    List<EventBuyVO> getRecommendedEvents(int limit);
    
    /**
     * 根據活動ID獲取該活動的票券類型列表
     * 
     * @param eventId 活動ID
     * @return 票券類型VO列表
     */
    List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId);
    
    /**
     * 計算票券類型的剩餘票數
     * 
     * @param typeId 票券類型ID
     * @return 剩餘票數
     */
    Integer calculateRemainingTickets(Integer typeId);
    
    /**
     * 檢查用戶是否已關注該活動
     * 
     * @param memberId 會員ID
     * @param eventId 活動ID
     * @return 關注狀態，1表示已關注，0表示未關注，null表示無記錄
     */
    Integer checkFavoriteStatus(Integer memberId, Integer eventId);
    
    /**
     * 插入新的關注記錄
     * 
     * @param favorite 關注記錄
     * @return 是否插入成功
     */
    boolean insertFavorite(FavoriteVO favorite);
    
    /**
     * 更新現有關注記錄
     * 
     * @param favorite 關注記錄
     * @return 是否更新成功
     */
    boolean updateFavorite(FavoriteVO favorite);
    
    /**
     * 計算活動的總剩餘票數
     *
     * @param eventId 活動ID
     * @return 總剩餘票數
     */
    Integer calculateTotalRemainingTickets(Integer eventId);
    
    /**
     * 獲取活動的圖片數據
     * 
     * @param eventId 活動ID
     * @return 圖片數據
     */
    byte[] getEventImage(Integer eventId);
    
}

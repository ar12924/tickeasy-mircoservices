package user.buy.dao;

import java.util.List;
import user.buy.vo.EventVO;
import user.buy.vo.TicketTypeVO;
/**
 * 活動資訊數據訪問接口
 */
public interface EventInfoDAO {
	/**
     * 根據活動ID獲取活動資訊
     * 
     * @param eventId 活動ID
     * @return 活動資訊VO
     */
    EventVO getEventInfoById(Integer eventId);
    
    /**
     * 獲取推薦活動列表
     * 
     * @param limit 限制數量
     * @return 活動資訊VO列表
     */
    List<EventVO> getRecommendedEvents(int limit);
    
    /**
     * 根據關鍵字搜索活動
     * 
     * @param keyword 關鍵字
     * @param offset 偏移量
     * @param limit 限制數量
     * @return 活動資訊VO列表
     */
    List<EventVO> searchEventsByKeyword(String keyword, int offset, int limit);
    
    /**
     * 根據活動ID獲取該活動的票券類型列表
     * 
     * @param eventId 活動ID
     * @return 票券類型VO列表
     */
    List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId);
    
    /**
     * 更新票券類型的剩餘票數
     * 
     * @param typeId 票券類型ID
     * @param quantity 減少的數量
     * @return 是否更新成功
     */
    boolean updateTicketTypeRemainingQuantity(Integer typeId, Integer quantity);
    
    /**
     * 檢查票券是否有足夠的剩餘數量
     * 
     * @param typeId 票券類型ID
     * @param quantity 需要的數量
     * @return 是否足夠
     */
    boolean hasEnoughRemainingTickets(Integer typeId, Integer quantity);
    
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
     * @param memberId 會員ID
     * @param eventId 活動ID
     * @param isFollowed 是否關注
     * @return 是否插入成功
     */
    boolean insertFavorite(Integer memberId, Integer eventId, Integer isFollowed);
    
    /**
     * 更新現有關注記錄
     * 
     * @param memberId 會員ID
     * @param eventId 活動ID
     * @param isFollowed 是否關注
     * @return 是否更新成功
     */
    boolean updateFavorite(Integer memberId, Integer eventId, Integer isFollowed);
    
    /**
     * 獲取活動圖片
     * 
     * @param eventId 活動ID
     * @return 圖片數據
     */
    byte[] getEventImage(Integer eventId);
}

package user.buy.dao;

import java.util.List;
import java.util.Map;
/**
 * 活動資訊數據訪問接口
 */
public interface EventInfoDAO {
	/**
     * 根據活動ID獲取活動資訊
     * 
     * @param eventId 活動ID
     * @return 活動資訊Map
     */
	Map<String, Object> getEventInfoById(Integer eventId);
	/**
     * 獲取推薦活動列表
     * 
     * @param limit 限制數量
     * @return 活動資訊Map列表
     */
	List<Map<String, Object>> getRecommendedEvents(int limit);
	/**
     * 根據關鍵字搜索活動
     * 
     * @param keyword 關鍵字
     * @param offset 偏移量
     * @param limit 限制數量
     * @return 活動資訊Map列表
     */
	List<Map<String, Object>> searchEventsByKeyword(String keyword, int offset, int limit);
	/**
     * 根據活動ID獲取該活動的票券類型列表
     * 
     * @param eventId 活動ID
     * @return 票券類型Map列表
     */
	List<Map<String, Object>> getEventTicketTypesByEventId(Integer eventId);
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
     * 設置用戶對活動的關注狀態
     * 
     * @param memberId 會員ID
     * @param eventId 活動ID
     * @param isFollowed 是否關注
     * @return 是否設置成功
     */
	boolean setEventFavoriteStatus(Integer memberId, Integer eventId, Integer isFollowed);
}

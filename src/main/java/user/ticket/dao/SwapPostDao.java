package user.ticket.dao;
/**
 * 換票貼文資料存取介面
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import user.ticket.vo.SwapPostVO;
import user.ticket.vo.MemberVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;
import java.util.List;
import java.util.Map;
public interface SwapPostDao {
	/**
     * 依活動ID查詢換票貼文列表
     * 
     * @param eventId 活動ID
     * @return 換票貼文資料列表
     */
    List<Map<String, Object>> listSwapPostsByEventId(Integer eventId);

    /**
     * 依ID查詢換票貼文
     * 
     * @param postId 貼文ID
     * @return 換票貼文資料
     */
    Map<String, Object> getSwapPostById(Integer postId);

    /**
     * 新增換票貼文
     * 
     * @param memberId 會員ID
     * @param ticketId 票券ID
     * @param description 貼文描述
     * @param eventId 活動ID
     * @return 新增的換票貼文資料
     */
    Map<String, Object> saveSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId);

    /**
     * 更新換票貼文
     * 
     * @param postId 貼文ID
     * @param description 貼文描述
     * @return 更新的換票貼文資料
     */
    Map<String, Object> updateSwapPost(Integer postId, String description);

    /**
     * 刪除換票貼文
     * 
     * @param postId 貼文ID
     */
    void removeSwapPost(Integer postId);

    /**
     * 依會員ID查詢換票貼文列表
     * 
     * @param memberId 會員ID
     * @return 換票貼文資料列表
     */
    List<Map<String, Object>> listSwapPostsByMemberId(Integer memberId);

    /**
     * 依票券ID查詢換票貼文
     * 
     * @param ticketId 票券ID
     * @return 換票貼文資料
     */
    Map<String, Object> getSwapPostByTicketId(Integer ticketId);

    /**
     * 依會員ID讀取會員照片
     * 
     * @param memberId 會員ID
     * @return 會員照片位元組陣列
     */
    byte[] getMemberPhoto(Integer memberId);

    /**
     * 檢查票券是否已存在換票貼文
     * 
     * @param ticketId 票券ID
     * @return 是否存在
     */
    boolean existsSwapPostByTicketId(Integer ticketId);

    /**
     * 檢查貼文擁有者
     * 
     * @param postId 貼文ID
     * @param memberId 會員ID
     * @return 是否為擁有者
     */
    boolean isPostOwner(Integer postId, Integer memberId);
}

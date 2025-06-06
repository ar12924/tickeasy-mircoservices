package user.ticket.service;
import java.util.List;
import java.util.Map;
/**
 * 票券交換服務介面
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
public interface TicketExchangeService {

    /**
     * 依活動ID查詢換票貼文列表
     * 
     * @param eventId 活動ID
     * @return 換票貼文資訊列表
     * @throws IllegalArgumentException 當eventId無效時
     */
    List<Map<String, Object>> listSwapPostsByEventId(Integer eventId);

    /**
     * 創建換票貼文
     * 
     * @param memberId 會員ID
     * @param ticketId 票券ID
     * @param description 貼文描述
     * @param eventId 活動ID
     * @return 創建的換票貼文資料
     * @throws IllegalArgumentException 當參數無效時
     * @throws RuntimeException 當票券已存在換票貼文時
     */
    Map<String, Object> createSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId);

    /**
     * 創建換票留言
     * 
     * @param postId 貼文ID
     * @param memberId 會員ID
     * @param ticketId 票券ID
     * @param description 留言描述
     * @return 創建的換票留言資料
     * @throws IllegalArgumentException 當參數無效時
     * @throws RuntimeException 當票券已存在換票留言時
     */
    Map<String, Object> createSwapComment(Integer postId, Integer memberId, Integer ticketId, String description);

    /**
     * 查詢會員的換票貼文
     * 
     * @param memberId 會員ID
     * @return 換票貼文列表
     * @throws IllegalArgumentException 當memberId無效時
     */
    List<Map<String, Object>> listMemberSwapPosts(Integer memberId);

    /**
     * 查詢會員的換票留言
     * 
     * @param memberId 會員ID
     * @return 換票留言列表
     * @throws IllegalArgumentException 當memberId無效時
     */
    List<Map<String, Object>> listMemberSwapComments(Integer memberId);

    /**
     * 刪除換票貼文
     * 
     * @param postId 貼文ID
     * @param memberId 會員ID
     * @throws IllegalArgumentException 當參數無效時
     * @throws RuntimeException 當貼文不存在或無權限時
     */
    void removeSwapPost(Integer postId, Integer memberId);

    /**
     * 查詢貼文的留言列表
     * 
     * @param postId 貼文ID
     * @return 留言列表
     * @throws IllegalArgumentException 當postId無效時
     */
    List<Map<String, Object>> listSwapCommentsByPostId(Integer postId);

    /**
     * 更新換票留言狀態
     * 
     * @param commentId 留言ID
     * @param status 狀態值
     * @param memberId 操作會員ID
     * @throws IllegalArgumentException 當參數無效時
     * @throws RuntimeException 當留言不存在或無權限時
     */
    void updateSwapCommentStatus(Integer commentId, Integer status, Integer memberId);
    
    /**
     * 根據暱稱獲取會員基本資訊
     * 
     * @param nickname 會員暱稱
     * @return 會員資訊
     * @throws IllegalArgumentException 當nickname無效時
     */
    Map<String, Object> getMemberByNickname(String nickname);
}
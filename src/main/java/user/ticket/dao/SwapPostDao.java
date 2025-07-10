package user.ticket.dao;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.EventTicketTypeVO;
import user.member.vo.Member;
import user.ticket.vo.SwapPostVO;
public interface SwapPostDao {
	/**
     * 依活動ID查詢換票貼文列表
     * 
     * @param eventId 活動ID
     * @return 換票貼文資料列表
     */
    List<SwapPostVO> listSwapPostsByEventId(Integer eventId);

    /**
     * 依ID查詢換票貼文
     * 
     * @param postId 貼文ID
     * @return 換票貼文資料
     */
    SwapPostVO getSwapPostById(Integer postId);

    /**
     * 新增換票貼文
     * 
     * @param memberId 會員ID
     * @param ticketId 票券ID
     * @param description 貼文描述
     * @param eventId 活動ID
     * @return 新增的換票貼文資料
     */
    SwapPostVO saveSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId);

    /**
     * 更新換票貼文
     * 
     * @param postId 貼文ID
     * @param description 貼文描述
     * @return 更新的換票貼文資料
     */
    SwapPostVO updateSwapPost(Integer postId, String description);

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
    List<SwapPostVO> listSwapPostsByMemberId(Integer memberId);

    /**
     * 依票券ID查詢換票貼文
     * 
     * @param ticketId 票券ID
     * @return 換票貼文資料
     */
    SwapPostVO getSwapPostByTicketId(Integer ticketId);

    /**
     * 依會員ID讀取會員照片
     * 
     * @param memberId 會員ID
     * @return 會員照片位元組陣列
     */
    byte[] getMemberPhoto(Integer memberId);
    
    InputStream getMemberPhotoStream(Integer memberId);

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
    
    /**
     * 根據暱稱查詢會員基本資訊
     * 
     * @param nickname 會員暱稱
     * @return 會員資訊
     */
    Member getMemberByNickname(String nickname);
    
    // 查會員票券
    List<BuyerTicketVO> getUserTickets(Integer memberId);
    
    Member getMemberById(Integer memberId);
    
    EventInfoVO getEventInfoById(Integer eventId);
    
    BuyerTicketVO getBuyerTicketById(Integer ticketId);
    
    EventTicketTypeVO getEventTicketTypeById(Integer typeId);
    
    /**
     * 獲取使用該票券的貼文數量
     */
    Long countPostsByTicketId(Integer ticketId);

    /**
     * 根據留言ID獲取貼文資訊
     */
    SwapPostVO getPostByCommentId(Integer commentId);
    
    /**
     * 檢查會員是否已對特定活動發布過換票貼文
     */
    boolean hasEventPostByMember(Integer memberId, Integer eventId);
    
    /**
     * 檢查票券是否已在留言中使用
     */
    boolean isTicketUsedInComment(Integer ticketId);
    
    /**
     * 獲取會員在特定活動的所有貼文
     */
    List<SwapPostVO> getMemberPostsByEvent(Integer memberId, Integer eventId);
    
    /**
     * 查詢獲取完整的換票貼文資訊
     */
    List<Map<String, Object>> listSwapPostsWithDetailsByEventId(Integer eventId);

    /**
     * 查詢獲取會員的換票貼文
     */
    List<Map<String, Object>> listSwapPostsWithDetailsByMemberId(Integer memberId);
}

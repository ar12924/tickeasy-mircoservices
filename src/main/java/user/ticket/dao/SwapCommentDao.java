package user.ticket.dao;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;
import user.member.vo.Member;
import user.ticket.vo.SwapCommentVO;
public interface SwapCommentDao {
	/**
     * 依貼文ID查詢換票留言列表
     * 
     * @param postId 貼文ID
     * @return 換票留言資料列表
     */
    List<SwapCommentVO> listSwapCommentsByPostId(Integer postId);

    /**
     * 依ID查詢換票留言
     * 
     * @param commentId 留言ID
     * @return 換票留言資料
     */
    SwapCommentVO getSwapCommentById(Integer commentId);

    /**
     * 新增換票留言
     * 
     * @param postId 貼文ID
     * @param memberId 會員ID
     * @param ticketId 票券ID
     * @param description 留言描述
     * @return 新增的換票留言資料
     */
    SwapCommentVO saveSwapComment(Integer postId, Integer memberId, Integer ticketId, String description);

    /**
     * 更新換票留言狀態
     * 
     * @param commentId 留言ID
     * @param status 狀態
     * @return 是否更新成功
     */
    boolean updateSwapCommentStatus(Integer commentId, Integer status);

    /**
     * 刪除換票留言
     * 
     * @param commentId 留言ID
     */
    void removeSwapComment(Integer commentId);

    /**
     * 依會員ID查詢換票留言列表
     * 
     * @param memberId 會員ID
     * @return 換票留言資料列表
     */
    List<SwapCommentVO> listSwapCommentsByMemberId(Integer memberId);

    /**
     * 依票券ID查詢換票留言
     * 
     * @param ticketId 票券ID
     * @return 換票留言資料
     */
    SwapCommentVO getSwapCommentByTicketId(Integer ticketId);

    /**
     * 依會員ID讀取會員照片
     * 
     * @param memberId 會員ID
     * @return 會員照片位元組陣列
     */
    byte[] getMemberPhoto(Integer memberId);
    
    InputStream getMemberPhotoStream(Integer memberId);

    /**
     * 檢查票券是否已存在換票留言
     * 
     * @param ticketId 票券ID
     * @return 是否存在
     */
    boolean existsSwapCommentByTicketId(Integer ticketId);

    /**
     * 檢查留言權限
     * 
     * @param commentId 留言ID
     * @param memberId 會員ID
     * @return 是否有權限
     */
    boolean hasCommentPermission(Integer commentId, Integer memberId);

    /**
     * 檢查貼文擁有者權限
     * 
     * @param commentId 留言ID
     * @param memberId 會員ID
     * @return 是否為貼文擁有者
     */
    boolean isPostOwnerByCommentId(Integer commentId, Integer memberId);
    
    Member getMemberById(Integer memberId);
    
    BuyerTicketVO getBuyerTicketById(Integer ticketId);
    
    EventTicketTypeVO getEventTicketTypeById(Integer typeId);
    
    /**
     * 獲取使用該票券的所有留言
     */
    List<SwapCommentVO> findCommentsByTicketId(Integer ticketId);
    
    /**
     * 查詢獲取貼文的留言
     */
    List<Map<String, Object>> listSwapCommentsWithDetailsByPostId(Integer postId);

    /**
     * 查詢獲取會員的留言
     */
    List<Map<String, Object>> listSwapCommentsWithDetailsByMemberId(Integer memberId);
}

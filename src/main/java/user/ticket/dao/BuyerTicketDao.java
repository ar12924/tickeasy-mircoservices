package user.ticket.dao;

import java.util.List;
import user.ticket.vo.BuyerTicketVO;

public interface BuyerTicketDao {
	
    /**
     * 檢查票券是否屬於指定會員
     */
    boolean checkTicketOwnership(Integer ticketId, Integer memberId);

    /**
     * 檢查票券是否存在
     */
    boolean ticketExists(Integer ticketId);

    /**
     * 檢查票券使用狀態
     */
    Integer getTicketUsedStatus(Integer ticketId);

    /**
     * 更新票券擁有者
     */
    boolean updateTicketOwner(Integer ticketId, Integer newOwnerId);

    /**
     * 根據ID獲取票券
     */
    BuyerTicketVO getTicketById(Integer ticketId);

    /**
     * 獲取會員的所有票券
     */
    List<BuyerTicketVO> getTicketsByMemberId(Integer memberId);
    
    /**
     * 獲取票券對應的活動ID
     */
    Integer getTicketEventId(Integer ticketId);
}
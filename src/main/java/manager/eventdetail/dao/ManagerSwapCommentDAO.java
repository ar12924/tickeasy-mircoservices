package manager.eventdetail.dao;

import manager.eventdetail.vo.ManagerSwapCommentVO;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 換票留言資料存取介面
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
public interface ManagerSwapCommentDAO {
    
	/**
     * 按建立時間查詢換票記錄
     */
    List<ManagerSwapCommentVO> findByCreateTime(Integer eventId, String keyword, Date startDate, Date endDate, Integer swappedStatus, Integer offset, Integer limit);
    
    /**
     * 按換票時間查詢換票記錄
     */
    List<ManagerSwapCommentVO> findBySwappedTime(Integer eventId, String keyword, Date startDate, Date endDate, Integer swappedStatus, Integer offset, Integer limit);
    
    /**
     * 按建立時間統計記錄數
     */
    Long countByCreateTime(Integer eventId, String keyword, Date startDate, Date endDate, Integer swappedStatus);
    
    /**
     * 按換票時間統計記錄數
     */
    Long countBySwappedTime(Integer eventId, String keyword, Date startDate, Date endDate, Integer swappedStatus);
    
    /**
     * 查詢有換票記錄的活動列表
     */
    List<Map<String, Object>> findEventList();
}
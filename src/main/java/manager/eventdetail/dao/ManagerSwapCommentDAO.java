package manager.eventdetail.dao;

import manager.eventdetail.vo.ManagerSwapCommentVO;
import java.util.List;
import java.util.Map;

/**
 * 換票留言資料存取介面
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
public interface ManagerSwapCommentDAO {
    
    /**
     * 根據條件查詢換票記錄（含分頁）
     * 
     * @param params 查詢參數
     * @param offset 起始位置
     * @param limit 每頁數量
     * @param orderBy 排序條件
     * @return 換票記錄列表
     */
    List<ManagerSwapCommentVO> findSwapsWithPaging(Map<String, Object> params, Integer offset, Integer limit, String orderBy);
    
    /**
     * 根據條件查詢換票記錄總數
     * 
     * @param params 查詢參數
     * @return 總數
     */
    Long countSwaps(Map<String, Object> params);
    
    /**
     * 查詢有換票記錄的活動列表
     * 
     * @return 活動列表
     */
    List<Map<String, Object>> findEventList();
}
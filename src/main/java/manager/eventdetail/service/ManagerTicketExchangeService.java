package manager.eventdetail.service;

import java.util.List;
import java.util.Map;

/**
 * 票券交換服務介面
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
public interface ManagerTicketExchangeService {
    
    /**
     * 查詢換票列表（含分頁和搜尋）
     * 
     * @param eventId 活動ID
     * @param keyword 搜尋關鍵字
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param swappedStatus 換票狀態
     * @param page 頁數
     * @param size 每頁數量
     * @return 分頁結果
     */
    Map<String, Object> getSwapExchangeList(Integer eventId, String keyword, String startDate, String endDate, Integer swappedStatus, Integer page, Integer size);
    
    /**
     * 獲取活動列表
     * 
     * @return 活動列表
     */
    List<Map<String, Object>> getEventList();

    /**
     * 獲取換票狀態選項
     * 
     * @return 狀態列表
     */
    List<Map<String, Object>> getSwapStatusList();
}

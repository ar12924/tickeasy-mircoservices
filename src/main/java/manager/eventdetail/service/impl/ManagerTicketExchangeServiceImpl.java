package manager.eventdetail.service.impl;

import manager.eventdetail.dao.ManagerSwapCommentDAO;
import manager.eventdetail.service.ManagerTicketExchangeService;
import manager.eventdetail.vo.ManagerSwapCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 票券交換服務實作類
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
@Service("managerTicketExchangeServiceImpl")
@Transactional
public class ManagerTicketExchangeServiceImpl implements ManagerTicketExchangeService {
    
    @Autowired
    @Qualifier("managerSwapCommentDAOImpl")
    private ManagerSwapCommentDAO swapCommentDAO;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSwapExchangeList(Integer eventId, String keyword, String startDate, String endDate, Integer swappedStatus, Integer page, Integer size) {
        // 建立查詢參數
        Map<String, Object> params = new HashMap<>();
        
        if (eventId != null) {
            params.put("eventId", eventId);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        if (swappedStatus != null) {
            params.put("swappedStatus", swappedStatus);
        }
        
        // 業務邏輯：根據換票狀態決定使用哪個時間欄位進行篩選
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                Date start = DATE_FORMAT.parse(startDate);
                if (swappedStatus != null && swappedStatus == 2) {
                    // 已完成狀態，使用換票時間篩選
                    params.put("swappedStartDate", start);
                } else {
                    // 其他狀態，使用建立時間篩選
                    params.put("createStartDate", start);
                }
            } catch (ParseException e) {
                throw new RuntimeException("日期格式錯誤: " + startDate, e);
            }
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                Date endDateTime = DATE_FORMAT.parse(endDate);
                // 設定為當天的23:59:59
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDateTime);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                Date end = cal.getTime();
                
                if (swappedStatus != null && swappedStatus == 2) {
                    // 已完成狀態，使用換票時間篩選
                    params.put("swappedEndDate", end);
                } else {
                    // 其他狀態，使用建立時間篩選
                    params.put("createEndDate", end);
                }
            } catch (ParseException e) {
                throw new RuntimeException("日期格式錯誤: " + endDate, e);
            }
        }
        
        // 業務邏輯：決定排序規則
        String orderBy;
        if (swappedStatus != null && swappedStatus == 2) {
            // 已完成狀態，按換票時間排序
            orderBy = "sc.swapped_time DESC";
        } else {
            // 其他狀態，按建立時間排序
            orderBy = "sc.create_time DESC";
        }
        
        // 計算分頁參數
        int currentPage = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : 10;
        int offset = (currentPage - 1) * pageSize;
        
        // 查詢數據
        List<ManagerSwapCommentVO> swapList = swapCommentDAO.findSwapsWithPaging(params, offset, pageSize, orderBy);
        Long totalCount = swapCommentDAO.countSwaps(params);
        
        // 計算總頁數
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        // 組裝返回結果
        Map<String, Object> result = new HashMap<>();
        result.put("data", processSwapList(swapList)); // 處理業務邏輯
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        result.put("hasNext", currentPage < totalPages);
        result.put("hasPrevious", currentPage > 1);
        
        return result;
    }
    
    /**
     * 處理換票列表的業務邏輯
     */
    private List<ManagerSwapCommentVO> processSwapList(List<ManagerSwapCommentVO> swapList) {
        // 這裡可以添加業務邏輯處理
        // 例如：狀態轉換、資料補充等
        return swapList;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEventList() {
        return swapCommentDAO.findEventList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSwapStatusList() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        // 從業務邏輯定義狀態選項
        Map<String, Object> status0 = new HashMap<>();
        status0.put("value", 0);
        status0.put("label", "待換票");
        statusList.add(status0);
        
        Map<String, Object> status1 = new HashMap<>();
        status1.put("value", 1);
        status1.put("label", "待確認");
        statusList.add(status1);
        
        Map<String, Object> status2 = new HashMap<>();
        status2.put("value", 2);
        status2.put("label", "已換票");
        statusList.add(status2);
        
        Map<String, Object> status3 = new HashMap<>();
        status3.put("value", 3);
        status3.put("label", "已取消");
        statusList.add(status3);
        
        return statusList;
    }
}
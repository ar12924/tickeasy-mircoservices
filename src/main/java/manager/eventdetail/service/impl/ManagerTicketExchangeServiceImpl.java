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
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final Integer MAX_PAGE_SIZE = 100;
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSwapExchangeList(Integer eventId, String keyword, String startDate, String endDate, Integer swappedStatus, Integer page, Integer size) {
        // 參數驗證
        validatePaginationParams(page, size);
        
        // 處理日期參數
        Date parsedStartDate = parseDate(startDate);
        Date parsedEndDate = parseEndDate(endDate);
        
        // 處理關鍵字
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        
        // 計算分頁參數
        int currentPage = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0 && size <= MAX_PAGE_SIZE) ? size : DEFAULT_PAGE_SIZE;
        int offset = (currentPage - 1) * pageSize;
        
        // 業務邏輯：根據換票狀態選擇查詢策略
        List<ManagerSwapCommentVO> swapList;
        Long totalCount;
        
        if (swappedStatus != null && swappedStatus == 2) {
            swapList = swapCommentDAO.findBySwappedTime(eventId, trimmedKeyword, parsedStartDate, parsedEndDate, swappedStatus, offset, pageSize);
            totalCount = swapCommentDAO.countBySwappedTime(eventId, trimmedKeyword, parsedStartDate, parsedEndDate, swappedStatus);
        } else {
            swapList = swapCommentDAO.findByCreateTime(eventId, trimmedKeyword, parsedStartDate, parsedEndDate, swappedStatus, offset, pageSize);
            totalCount = swapCommentDAO.countByCreateTime(eventId, trimmedKeyword, parsedStartDate, parsedEndDate, swappedStatus);
        }
        
        // 計算總頁數
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return buildPaginationResult(swapList, totalCount, totalPages, currentPage, pageSize);
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
        statusList.add(createStatusOption(0, "待換票"));
        statusList.add(createStatusOption(1, "待確認"));
        statusList.add(createStatusOption(2, "已換票"));
        statusList.add(createStatusOption(3, "已取消"));
        
        return statusList;
    }
    
    /**
     * 驗證分頁參數
     */
    private void validatePaginationParams(Integer page, Integer size) {
        if (page != null && page <= 0) {
            throw new IllegalArgumentException("頁數必須大於0");
        }
        if (size != null && (size <= 0 || size > MAX_PAGE_SIZE)) {
            throw new IllegalArgumentException("每頁數量必須在1-" + MAX_PAGE_SIZE + "之間");
        }
    }
    
    /**
     * 解析開始日期
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(dateStr.trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("開始日期格式錯誤，請使用yyyy-MM-dd格式");
        }
    }
    
    /**
     * 解析結束日期
     */
    private Date parseEndDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            Date endDate = DATE_FORMAT.parse(dateStr.trim());
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            return cal.getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("結束日期格式錯誤，請使用yyyy-MM-dd格式");
        }
    }
    
    /**
     * 建立分頁結果
     */
    private Map<String, Object> buildPaginationResult(List<ManagerSwapCommentVO> swapList, Long totalCount, int totalPages, int currentPage, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", swapList);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        result.put("hasNext", currentPage < totalPages);
        result.put("hasPrevious", currentPage > 1);
        
        return result;
    }
    
    /**
     * 建立狀態選項
     */
    private Map<String, Object> createStatusOption(Integer value, String label) {
        Map<String, Object> status = new HashMap<>();
        status.put("value", value);
        status.put("label", label);
        return status;
    }
}
package user.ticket.service;

import java.util.Map;

/**
 * 會員查詢服務介面
 * 創建者: archchang
 * 創建日期: 2025-06-05
 */
public interface MemberLookupService {
    
    /**
     * 根據暱稱獲取會員基本資訊
     * 
     * @param nickname 會員暱稱
     * @return 會員資訊（包含memberId, nickname等）
     */
    Map<String, Object> getMemberByNickname(String nickname);
}

package user.ticket.dao;

import java.util.Map;

/**
 * 會員查詢資料存取介面
 * 創建者: archchang
 * 創建日期: 2025-06-05
 */
public interface MemberLookupDao {
    
    /**
     * 根據暱稱查詢會員基本資訊
     * 
     * @param nickname 會員暱稱
     * @return 會員資訊
     */
    Map<String, Object> findByNickname(String nickname);
}

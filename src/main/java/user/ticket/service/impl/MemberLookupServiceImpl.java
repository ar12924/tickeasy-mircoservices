package user.ticket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.ticket.dao.MemberLookupDao;
import user.ticket.service.MemberLookupService;

import java.util.Map;

/**
 * 會員查詢服務實作類
 * 創建者: archchang
 * 創建日期: 2025-06-05
 */
@Service
@Transactional
public class MemberLookupServiceImpl implements MemberLookupService {

    @Autowired
    private MemberLookupDao memberLookupDao;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMemberByNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return null;
        }
        
        try {
            return memberLookupDao.findByNickname(nickname.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
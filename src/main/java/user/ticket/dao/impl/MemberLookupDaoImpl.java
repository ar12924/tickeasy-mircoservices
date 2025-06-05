package user.ticket.dao.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import common.dao.CommonDao;
import user.ticket.dao.MemberLookupDao;
import user.ticket.vo.MemberVO;

import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 會員查詢資料存取實作類
 * 創建者: 系統
 * 創建日期: 2025-06-05
 */
@Repository
public class MemberLookupDaoImpl implements MemberLookupDao {

    @PersistenceContext
    private Session session;

    @Override
    public Map<String, Object> findByNickname(String nickname) {
        try {
            String hql = "FROM MemberVO m WHERE m.nickName = :nickname AND m.active = 1";
            List<MemberVO> results = session
                    .createQuery(hql, MemberVO.class)
                    .setParameter("nickname", nickname)
                    .setMaxResults(1)
                    .getResultList();
                    
            if (results.isEmpty()) {
                return null;
            }
            
            MemberVO member = results.get(0);
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickname", member.getNickName());
            memberInfo.put("email", member.getEmail());
            memberInfo.put("roleLevel", member.getRoleLevel());
            
            return memberInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

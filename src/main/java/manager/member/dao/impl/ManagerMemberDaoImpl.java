package manager.member.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import manager.member.dao.ManagerMemberDao;
import user.member.vo.Member;

import java.util.List;
import java.util.Map;


/**
 * 會員資料存取實作類
 * 創建者: archchang
 * 創建日期: 2025-06-25
 */
@Repository
public class ManagerMemberDaoImpl implements ManagerMemberDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM member ORDER BY create_time DESC";
        NativeQuery<Member> query = getCurrentSession().createNativeQuery(sql, Member.class);
        return query.getResultList();
    }

    @Override
    public Member findById(Integer memberId) {
        return getCurrentSession().get(Member.class, memberId);
    }

    @Override
    public List<Member> findMembersWithConditions(String userName, String startDate, String endDate,
                                                 Integer roleLevel, Integer isActive, int offset, int limit) {

        StringBuilder sql = new StringBuilder("SELECT * FROM member WHERE 1=1");
        
        buildWhereConditions(sql, userName, startDate, endDate, roleLevel, isActive);
        
        sql.append(" ORDER BY create_time DESC");
        
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        }
        
        NativeQuery<Member> query = getCurrentSession().createNativeQuery(sql.toString(), Member.class);
        
        setQueryParameters(query, userName, startDate, endDate, roleLevel, isActive);
        
        return query.getResultList();
    }

    @Override
    public long countMembersWithConditions(String userName, String startDate, String endDate,
                                          Integer roleLevel, Integer isActive) {
    
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM member WHERE 1=1");
        
        
        buildWhereConditions(sql, userName, startDate, endDate, roleLevel, isActive);
        
        
        NativeQuery<Number> query = getCurrentSession().createNativeQuery(sql.toString());
        
        
        setQueryParameters(query, userName, startDate, endDate, roleLevel, isActive);
        
        Number result = query.uniqueResult();
        return result != null ? result.longValue() : 0L;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM member";
        NativeQuery<Number> query = getCurrentSession().createNativeQuery(sql);
        Number result = query.uniqueResult();
        return result != null ? result.longValue() : 0L;
    }

    /**
     * DAO層責任：構建 WHERE 條件子句（Native SQL）
     * 使用實際的資料庫欄位名稱，根據條件動態構建 SQL
     */
    private void buildWhereConditions(StringBuilder sql, String userName, String startDate, 
                                     String endDate, Integer roleLevel, Integer isActive) {
        
        
        if (userName != null && !userName.trim().isEmpty()) {
            sql.append(" AND (user_name LIKE :userName OR nick_name LIKE :userName)");
        }
        
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND DATE(create_time) >= :startDate");
        }
        
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND DATE(create_time) <= :endDate");
        }
        
        
        if (roleLevel != null) {
            sql.append(" AND role_level = :roleLevel");
        }
        
        
        if (isActive != null) {
            sql.append(" AND is_active = :isActive");
        }
    }

    /**
     * DAO層責任：設定 Native Query 參數
     * 處理參數值的設定和格式化
     */
    private void setQueryParameters(NativeQuery<?> query, String userName, String startDate, 
                                   String endDate, Integer roleLevel, Integer isActive) {
        
        
        if (userName != null && !userName.trim().isEmpty()) {
            query.setParameter("userName", "%" + userName.trim() + "%");
        }
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            query.setParameter("startDate", startDate);
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            query.setParameter("endDate", endDate);
        }
        
        if (roleLevel != null) {
            query.setParameter("roleLevel", roleLevel);
        }

        if (isActive != null) {
            query.setParameter("isActive", isActive);
        }
    }
    
    @Override
    public Member save(Member member) {
        getCurrentSession().persist(member);
        return member;
    }

    @Override
    public Member update(Member member) {
        return (Member) getCurrentSession().merge(member);
    }

    @Override
    public void deleteById(Integer memberId) {
    	try {
    		// 1. 刪除間接相關記錄（必須先刪除，避免外鍵約束）
            String deleteSwapCommentsSQL = "DELETE FROM swap_comment WHERE comment_member_id = :memberId";
            getCurrentSession().createNativeQuery(deleteSwapCommentsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
            
            // 2. 刪除直接相關記錄（會阻止會員刪除的記錄）
            String deleteSwapPostsSQL = "DELETE FROM swap_post WHERE post_member_id = :memberId";
            getCurrentSession().createNativeQuery(deleteSwapPostsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
                    
            String deleteDistTicketsSQL = "DELETE FROM dist_ticket WHERE received_member_id = :memberId";
            getCurrentSession().createNativeQuery(deleteDistTicketsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
                    
            String deleteFavoritesSQL = "DELETE FROM favorite WHERE member_id = :memberId";
            getCurrentSession().createNativeQuery(deleteFavoritesSQL)
                    .setParameter("memberId", memberId).executeUpdate();
                    
            String deleteNotificationsSQL = "DELETE FROM member_notification WHERE member_id = :memberId";
            getCurrentSession().createNativeQuery(deleteNotificationsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
            
            // 3. 更新業務相關記錄（保留記錄但解除關聯）
            String updateOrdersSQL = "UPDATE buyer_order SET member_id = NULL WHERE member_id = :memberId";
            getCurrentSession().createNativeQuery(updateOrdersSQL)
                    .setParameter("memberId", memberId).executeUpdate();
                    
            String updateTicketsSQL = "UPDATE buyer_ticket SET current_holder_member_id = NULL WHERE current_holder_member_id = :memberId";
            getCurrentSession().createNativeQuery(updateTicketsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
                    
            String updateEventsSQL = "UPDATE event_info SET member_id = NULL WHERE member_id = :memberId";
            getCurrentSession().createNativeQuery(updateEventsSQL)
                    .setParameter("memberId", memberId).executeUpdate();
            
            // 最後刪除會員記錄
            Member member = findById(memberId);
            if (member != null) {
                getCurrentSession().delete(member);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("刪除會員失敗：" + e.getMessage(), e);
        }
    }

    @Override
    public Member findByUserName(String userName) {
        String sql = "SELECT * FROM member WHERE user_name = :userName";
        NativeQuery<Member> query = getCurrentSession().createNativeQuery(sql, Member.class);
        query.setParameter("userName", userName);
        return query.uniqueResult();
    }

    @Override
    public Member findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = :email";
        NativeQuery<Member> query = getCurrentSession().createNativeQuery(sql, Member.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }
    
    @Override
    public Member findByPhone(String phone) {
        String sql = "SELECT * FROM member WHERE phone = :phone";
        NativeQuery<Member> query = getCurrentSession().createNativeQuery(sql, Member.class);
        query.setParameter("phone", phone);
        List<Member> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
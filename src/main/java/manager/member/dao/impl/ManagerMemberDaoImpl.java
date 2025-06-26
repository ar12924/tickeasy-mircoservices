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
}
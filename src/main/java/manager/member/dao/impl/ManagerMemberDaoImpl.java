package manager.member.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Member> findAll() {
        return getSession().createQuery("FROM Member ORDER BY createTime DESC", Member.class).getResultList();
    }

    @Override
    public Member findById(Integer memberId) {
        return getSession().get(Member.class, memberId);
    }

    @Override
    public List<Member> findByUserName(String userName) {
        return getSession().createQuery(
                "FROM Member WHERE userName LIKE :userName OR nickName LIKE :userName ORDER BY createTime DESC",
                Member.class)
                .setParameter("userName", "%" + userName + "%")
                .getResultList();
    }

    @Override
    public List<Member> findByRoleLevel(Integer roleLevel) {
        return getSession()
                .createQuery("FROM Member WHERE roleLevel = :roleLevel ORDER BY createTime DESC", Member.class)
                .setParameter("roleLevel", roleLevel)
                .getResultList();
    }

    @Override
    public List<Member> findByIsActive(Integer isActive) {
        return getSession()
                .createQuery("FROM Member WHERE isActive = :isActive ORDER BY createTime DESC", Member.class)
                .setParameter("isActive", isActive)
                .getResultList();
    }

    @Override
    public List<Member> findByDateRange(String startDate, String endDate) {
        return getSession()
                .createQuery("FROM Member WHERE DATE(createTime) BETWEEN :startDate AND :endDate ORDER BY createTime DESC",
                        Member.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<Member> findAllWithPaging(int offset, int limit) {
        return getSession()
                .createQuery("FROM Member ORDER BY createTime DESC", Member.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long count() {
        return getSession().createQuery("SELECT COUNT(*) FROM Member", Long.class).uniqueResult();
    }

    @Override
    public List<Member> findByDynamicQuery(String hql, Map<String, Object> parameters, int offset, int limit) {
        Query<Member> query = getSession().createQuery(hql, Member.class);
        
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        
        if (offset >= 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        
        return query.getResultList();
    }

    @Override
    public long countByDynamicQuery(String hql, Map<String, Object> parameters) {
        Query<Long> query = getSession().createQuery(hql, Long.class);
        
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        
        return query.uniqueResult();
    }
}
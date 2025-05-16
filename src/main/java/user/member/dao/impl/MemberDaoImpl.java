package user.member.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import common.util.HibernateUtil5;
import user.member.dao.MemberDao;
import user.member.entity.Member;


public class MemberDaoImpl implements MemberDao {


	@Override
	public boolean insert(Member member) {
		Transaction tx = null;
	    try (
	    		Session session = HibernateUtil5.getSessionFactory().openSession())
	    	{
	    	tx = session.beginTransaction();
	    		session.save(member);
	    		tx.commit();
	    		return true;

	        } catch (Exception e) {
	        	if (tx != null) tx.rollback();
	            e.printStackTrace();
	            return false;
	        }
	    }
	

	@Override
	public boolean update(Member member) {
		   Transaction tx = null;
	        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
	            tx = session.beginTransaction();
	            session.update(member);
	            tx.commit();
	            return true;
	        } catch (Exception e) {
	            if (tx != null) tx.rollback();
	            e.printStackTrace();
	            return false;
	        }
	    }

	@Override
	public Member findByUserName(String userName) {
        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
            String hql = "FROM Member WHERE userName = :userName";
            Query<Member> query = session.createQuery(hql, Member.class);
            query.setParameter("userName", userName);
            return query.uniqueResult();
        }
    }

	@Override
	public Member findById(int memberId) {
        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
            return session.get(Member.class, memberId);
        }
    }

	@Override
	public boolean delete(int memberId) {
        Transaction tx = null;
        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
            Member member = session.get(Member.class, memberId);
            if (member == null) return false;
            tx = session.beginTransaction();
            session.delete(member);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public List<Member> listAll() {
		   try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
	            String hql = "FROM Member";
	            return session.createQuery(hql, Member.class).list();
	        }
	}
	

}


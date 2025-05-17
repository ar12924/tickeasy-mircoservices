package user.member.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import common.util.HibernateUtil5;
import user.member.dao.VerificationDao;
import user.member.entity.VerificationToken;

public class VerificationDaoImpl implements VerificationDao{

	@Override
	public boolean insert(VerificationToken token) {
        Transaction tx = null;
        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(token);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
	}

	@Override
	public VerificationToken findByToken(String tokenStr) {
		 try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
	          String hql = "FROM VerificationToken WHERE token = :token";
	          Query<VerificationToken> query = session.createQuery(hql, VerificationToken.class);
	          query.setParameter("token", tokenStr);
	          return query.uniqueResult();
	       
		 }
	
	}

	@Override
	public boolean update(VerificationToken token) {
		Transaction tx = null;
        try (Session session = HibernateUtil5.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(token);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
	}
	

}

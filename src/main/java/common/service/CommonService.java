package common.service;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import common.util.HibernateUtil5;

public interface CommonService {
	private SessionFactory getSessionFactory() {
		return HibernateUtil5.getSessionFactory();
	}

	default Transaction beginTxn() {
		return getSessionFactory().getCurrentSession().beginTransaction();
	}

	default void commit() {
		getSessionFactory().getCurrentSession().getTransaction().commit();
	}

	default void rollback() {
		getSessionFactory().getCurrentSession().getTransaction().rollback();
	}
}

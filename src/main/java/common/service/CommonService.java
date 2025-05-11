package common.service;

import org.hibernate.Transaction;

import common.util.HibernateUtil5;

public interface CommonService {
	default Transaction beginTxn() {
		return HibernateUtil5.getSessionFactory().getCurrentSession().beginTransaction();
	}

	default void commit() {
		HibernateUtil5.getSessionFactory().getCurrentSession().getTransaction().commit();
	}

	default void rollback() {
		HibernateUtil5.getSessionFactory().getCurrentSession().getTransaction().rollback();
	}
}

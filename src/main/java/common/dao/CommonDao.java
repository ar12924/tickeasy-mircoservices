package common.dao;

import org.hibernate.Session;

import common.util.HibernateUtil5;

public interface CommonDao {

	default Session getSession() {
		return HibernateUtil5.getSessionFactory().getCurrentSession();
	}
}

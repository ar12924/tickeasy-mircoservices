package common.filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import common.util.HibernateUtil5;

//@WebFilter("/*")
public class HibernateFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
		Session session = HibernateUtil5.getSessionFactory().getCurrentSession();
		try {
			Transaction transaction = session.beginTransaction();
			chain.doFilter(req, res);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
	}
}

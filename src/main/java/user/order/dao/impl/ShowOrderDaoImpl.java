package user.order.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.order.dao.ShowOrderDao;
import user.order.vo.BuyerOrderDC;

@Repository
public class ShowOrderDaoImpl implements ShowOrderDao {

	@PersistenceContext
	private Session session;

	public List<BuyerOrderDC> findAllOrders() {
		String hql = "FROM buyer_order ORDER BY order_id DESC";
		return session.createQuery(hql, user.order.vo.BuyerOrderDC.class).getResultList();
	}
}
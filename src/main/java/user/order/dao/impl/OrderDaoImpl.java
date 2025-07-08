package user.order.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.order.dao.OrderDao;
import user.order.vo.BuyerOrderDC;

@Repository
public class OrderDaoImpl implements OrderDao {

	@PersistenceContext
	private Session session;

	@Override
	public List<BuyerOrderDC> findAllOrders(Integer memberId) {
		String hql = "FROM BuyerOrderDC WHERE memberId = :memberId ORDER BY order_id DESC";
		return session.createQuery(hql, user.order.vo.BuyerOrderDC.class).getResultList();
	}
}
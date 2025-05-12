package user.buy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.query.Query;

import common.util.HibernateUtil5;
import user.buy.dao.BuyDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class BuyDaoImpl implements BuyDao {
	private DataSource ds;

	public BuyDaoImpl() {
		try {
			// 取得 ds (所有 CRUD 共用)
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<EventInfo> selectEventByKeyword(String keyword) {
		// 1. 生成 HQL 語句
		String hql = "FROM EventInfo WHERE eventName LIKE :keyword ORDER BY eventFromDate";
		// 2. 條件式查詢
		Query<EventInfo> query = getSession().createQuery(hql, EventInfo.class);
		// 3. 判斷 keyword 字串是否為空
		if (!keyword.isEmpty()) {
			query.setParameter("keyword", "%" + keyword + "%");
		} else {
			query.setParameter("keyword", "%%");
		}
		return query.getResultList();
	}

	@Override
	public List<BuyerTicket> selectTicket() {
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM BuyerTicket";
		// 2. 查詢所有 row
		return getSession().createQuery(hql, BuyerTicket.class).getResultList();
	}

	@Override
	public List<MemberNotification> selectNotification() {
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM MemberNotification ORDER BY sendTime";
		return getSession().createQuery(hql, MemberNotification.class).getResultList();
	}
}

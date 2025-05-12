package user.buy.dao.impl;

import java.util.List;

import org.hibernate.query.Query;

import user.buy.dao.SearchDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class SearchDaoImpl implements SearchDao {
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

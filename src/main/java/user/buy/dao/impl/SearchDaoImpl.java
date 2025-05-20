package user.buy.dao.impl;

import java.util.List;

import org.hibernate.query.Query;

import common.vo.Payload;
import user.buy.dao.SearchDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class SearchDaoImpl implements SearchDao {

	@Override
	public Payload<List<EventInfo>> selectEventByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
		// 1. 生成 HQL 語句
		String hqlData = "FROM EventInfo WHERE eventName LIKE :keyword ORDER BY eventFromDate";
		String hqlCount = "SELECT COUNT(e) FROM EventInfo e WHERE e.eventName LIKE :keyword";
		// 2. 條件式查詢
		Query<EventInfo> queryData = getSession().createQuery(hqlData, EventInfo.class)
				.setFirstResult((pageNumber - 1) * pageSize) // 略過比數
				.setMaxResults(pageSize); // 顯示比數
		Query<Long> queryCount = getSession().createQuery(hqlCount, Long.class);
		// 3. 判斷 keyword 字串是否為空
		if (!keyword.isEmpty()) {
			queryData.setParameter("keyword", "%" + keyword + "%");
			queryCount.setParameter("keyword", "%" + keyword + "%");
		} else {
			queryData.setParameter("keyword", "%%");
			queryCount.setParameter("keyword", "%%");
		}
		// 4. 將查詢結果和總筆數放入 Payload 型態
		Payload<List<EventInfo>> eventPayload = new Payload<>();
		eventPayload.setData(queryData.getResultList()); // 查詢結果
		eventPayload.setCount(queryCount.uniqueResult()); // 總筆數
		return eventPayload;
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

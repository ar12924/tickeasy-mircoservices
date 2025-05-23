package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.SearchDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

@Repository
public class SearchDaoImpl implements SearchDao {
	@PersistenceContext
	private Session session;

	@Override
	public List<EventInfo> selectEventByKeywordWithPages(String keyword, Integer pageNumber, Integer pageSize) {
		// 1. 判斷 keyword 字串是否為空
		boolean isKeyword = keyword.isEmpty();
		// 2. 生成 HQL 語句
		StringBuilder hqlDataTmp = new StringBuilder("FROM EventInfo ");
		if (isKeyword) {
			hqlDataTmp.append("ORDER BY eventFromDate");
		} else {
			hqlDataTmp.append("WHERE eventName LIKE :keyword ORDER BY eventFromDate");
		}
		String hqlData = hqlDataTmp.toString();
		// 3. 查詢(含 LIMIT)
		Query<EventInfo> queryData = null;
		if (isKeyword) {
			queryData = getSession().createQuery(hqlData, EventInfo.class).setFirstResult((pageNumber - 1) * pageSize) // 略過比數
					.setMaxResults(pageSize); // 顯示比數
		} else {
			queryData = getSession().createQuery(hqlData, EventInfo.class).setParameter("keyword", "%" + keyword + "%")
					.setFirstResult((pageNumber - 1) * pageSize) // 略過比數
					.setMaxResults(pageSize); // 顯示比數
		}
		// 4. 將查詢結果和總筆數放入 List 型態
		return queryData.getResultList(); // 查詢結果
	}

	public Long selectEventCountByKeyword(String keyword) {
		// 1. 判斷 keyword 字串是否為空
		boolean isKeyword = keyword.isEmpty();
		// 2. 生成 HQL 語句
		StringBuilder hqlCountTmp = new StringBuilder("SELECT COUNT(e) FROM EventInfo e ");
		if (!isKeyword) {
			hqlCountTmp.append("WHERE e.eventName LIKE :keyword");
		}
		String hqlCount = hqlCountTmp.toString();
		Query<Long> queryCount = null;
		// 3. 查詢整張表並聚合
		if (!isKeyword) {
			queryCount = getSession().createQuery(hqlCount, Long.class).setParameter("keyword", "%" + keyword + "%");
		} else {
			queryCount = getSession().createQuery(hqlCount, Long.class);
		}
		// 4. 回傳總筆數
		return queryCount.uniqueResult();
	}

	@Override
	public List<BuyerTicket> selectTicket() {
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM BuyerTicket";
		// 2. 查詢所有 row
		return session.createQuery(hql, BuyerTicket.class).getResultList();
	}

	@Override
	public List<MemberNotification> selectNotification() {
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM MemberNotification ORDER BY sendTime";
		return session.createQuery(hql, MemberNotification.class).getResultList();
	}
}

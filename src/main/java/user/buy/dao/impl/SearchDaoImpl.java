package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import common.vo.Order;
import user.buy.dao.SearchDao;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;

@Repository
public class SearchDaoImpl implements SearchDao {
	@PersistenceContext
	private Session session;

	/**
	 * 查詢活動資料。
	 *
	 * @param {String}  searchTerm - 輸入關鍵字。
	 * @param {Integer} page - 第幾頁。
	 * @param {Order}   order - 排序方法(DESC/ASC)。
	 * @return {List<EventInfo>} 查詢活動結果。
	 */
	@Override
	public List<EventInfo> selectEventInfo(String searchTerm, Integer page, Order order, Integer pageSize) {
		// HQL 語句建構器
		var hqlTmp = new StringBuilder("FROM EventInfo ei WHERE ei.isPosted = 1 ");

		// 判斷是否查詢關鍵字(不判斷會影響查詢效率)
		if (StringUtils.hasText(searchTerm)) {
			hqlTmp.append("AND ei.eventName LIKE :searchTerm ");
		}

		// 追加查詢時間及排序
		if (order.equals(Order.DESC)) {
			hqlTmp.append("ORDER BY ei.eventFromDate DESC");
		}
		if (order.equals(Order.ASC)) {
			hqlTmp.append("ORDER BY ei.eventFromDate ASC");
		}

		// 執行查詢
		String hql = hqlTmp.toString();
		Query<EventInfo> query = session.createQuery(hql, EventInfo.class).setFirstResult((page - 1) * pageSize) // 略過筆數(從...開始)
				.setMaxResults(pageSize); // 顯示筆數

		// 判斷要查詢關鍵字
		if (StringUtils.hasText(searchTerm)) {
			query.setParameter("searchTerm", "%" + searchTerm + "%");
		}

		return query.getResultList();
	}
	
	/**
	 * 活動資料查詢筆數。(配合 selectEventInfo() 的方法)
	 *
	 * @param {String}  searchTerm - 輸入關鍵字。
	 * @return {Long} 查詢筆數。
	 */
	@Override
	public Long countEventInfo(String searchTerm) {
		// HQL 語句建構器
		var hqlTmp = new StringBuilder("SELECT COUNT(ei) FROM EventInfo ei WHERE ei.isPosted = 1 ");

		// 判斷是否查詢關鍵字(不判斷會影響查詢效率)
		if (StringUtils.hasText(searchTerm)) {
			hqlTmp.append("AND ei.eventName LIKE :searchTerm ");
		}

		// 執行查詢
		String hql = hqlTmp.toString();
		Query<Long> query = session.createQuery(hql, Long.class);

		// 判斷要查詢關鍵字
		if (StringUtils.hasText(searchTerm)) {
			query.setParameter("searchTerm", "%" + searchTerm + "%");
		}

		return query.uniqueResult();
	}

	/**
	 * 透過 memberId 查詢我的關注。
	 *
	 * @param {Integer} memberId - 會員 id。
	 * @return List<Favorite> n 筆活動資料。
	 */
	@Override
	public List<Favorite> selectAllFavoriteByMemberId(Integer memberId) {
		// 1. HQL 語句
		var hql = "FROM Favorite WHERE memberId = :memberId";

		// 2. 查詢
		Query<Favorite> query = session.createQuery(hql, Favorite.class);
		query.setParameter("memberId", memberId);
		return query.getResultList();
	}

	/**
	 * 抓著 eventId 儲存至我的關注。 (必須先有 session.member)
	 *
	 * @param {Integer} eventId - 活動 id。
	 * @param {Integer} memberId - 會員 id。
	 * @return {Integer} 新增實體物件的識別 id。
	 */
	@Override
	public Integer insertFavorite(Integer eventId, Integer memberId) {
		var favorite = new Favorite();

		// 1. 建立資料實體物件
		favorite.setEventId(eventId);
		favorite.setMemberId(memberId);
		favorite.setIsFollowed(1);

		// 2. 查詢
		session.persist(favorite);
		session.flush(); // 強制執行 SQL，生成 id

		// 3. 取得自動生成的 id
		return favorite.getFavoriteId();
	}

	/*
	 * 抓著 eventId 移除至我的關注中對應的資料。 (必須先有 session.member)
	 *
	 * @param {Integer} eventId - 活動 id。
	 * 
	 * @param {Integer} memberId - 會員 id。
	 * 
	 * @return {Integer} 刪除實體物件筆數。
	 */
	@Override
	public Integer removeFavorite(Integer eventId, Integer memberId) {
		// 1. HQL 語句
		String hql = "DELETE Favorite f WHERE f.eventId = :eventId AND f.memberId = :memberId";
		// 2. 執行刪除 by (memberId, eventId)
		return session.createQuery(hql).setParameter("eventId", eventId).setParameter("memberId", memberId)
				.executeUpdate();
	}

	/**
	 * 透過 keywordId 查詢 keyword 名稱。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {KewordCategory} 對應活動的所有關鍵字。
	 */
	@Override
	public KeywordCategory selectKeywordByKeywordId(Integer keywordId) {
		// 1. HQL 語句
		var hql = "FROM KeywordCategory kc WHERE keywordId = :keywordId";

		// 2. 查詢對應的所有關鍵字
		Query<KeywordCategory> query = session.createQuery(hql, KeywordCategory.class);
		query.setParameter("keywordId", keywordId);
		return query.uniqueResult();
	}

	/**
	 * 傳入關鍵字、頁數，查詢活動資料表
	 * 
	 * @param 關鍵字, 頁數, 每頁筆數
	 * @return 符合條件的數筆活動資料
	 */
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
			queryData = session.createQuery(hqlData, EventInfo.class).setFirstResult((pageNumber - 1) * pageSize) // 略過比數
					.setMaxResults(pageSize); // 顯示比數
		} else {
			queryData = session.createQuery(hqlData, EventInfo.class).setParameter("keyword", "%" + keyword + "%")
					.setFirstResult((pageNumber - 1) * pageSize) // 略過比數
					.setMaxResults(pageSize); // 顯示比數
		}
		// 4. 將查詢結果和總筆數放入 List 型態
		return queryData.getResultList(); // 查詢結果
	}

	/**
	 * 傳入關鍵字查詢活動資料表
	 * 
	 * @param 關鍵字
	 * @return 活動資料表總筆數
	 */
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
			queryCount = session.createQuery(hqlCount, Long.class).setParameter("keyword", "%" + keyword + "%");
		} else {
			queryCount = session.createQuery(hqlCount, Long.class);
		}
		// 4. 回傳總筆數
		return queryCount.uniqueResult();
	}
}

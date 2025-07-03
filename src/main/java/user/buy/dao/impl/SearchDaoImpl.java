package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.SearchDao;
import user.buy.vo.EventInfo;
import user.buy.vo.KeywordCategory;

@Repository
public class SearchDaoImpl implements SearchDao {
    @PersistenceContext
    private Session session;

    /**
     * 查詢 n 筆活動資料。
     * @param {Integer} n - 筆數
     * @return List<EventInfo> n 筆活動資料。
     */
    @Override
	public List<EventInfo> selectRecentEventInfo(Integer n) {
    	// 1. HQL 語句
    	var hql = "FROM EventInfo ei WHERE ei.isPosted = 1 ORDER BY ei.eventFromDate ASC";
    	
    	// 2. 查詢前 n 筆
    	Query<EventInfo> query = session.createQuery(hql, EventInfo.class)
    			.setFirstResult(0) // 略過筆數
    			.setMaxResults(n); // 顯示筆數
    	return query.getResultList();
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
            queryData = session.createQuery(hqlData, EventInfo.class)
                    .setFirstResult((pageNumber - 1) * pageSize) // 略過比數
                    .setMaxResults(pageSize); // 顯示比數
        } else {
            queryData = session.createQuery(hqlData, EventInfo.class)
                    .setParameter("keyword", "%" + keyword + "%")
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
            queryCount = session.createQuery(hqlCount, Long.class)
                    .setParameter("keyword", "%" + keyword + "%");
        } else {
            queryCount = session.createQuery(hqlCount, Long.class);
        }
        // 4. 回傳總筆數
        return queryCount.uniqueResult();
    }
}

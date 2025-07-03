package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import user.buy.dao.SearchDao;
import user.buy.service.SearchService;
import user.buy.vo.EventInfo;
import user.buy.vo.KeywordCategory;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private SearchDao dao;

	/**
	 * 查詢近期 n 筆活動資料。
	 * 
	 * @return {EventInfo} 回應近期活動資料。
	 */
	@Transactional
	@Override
	public List<EventInfo> getRecentEventInfo(Integer n) {
		return dao.selectRecentEventInfo(n);
	}

	/**
	 * 透過 keywordId 查詢 keyword 名稱。
	 * 
	 * @return {EventInfo} 回應近期活動資料。
	 */
	@Transactional
	@Override
	public KeywordCategory getKeyword(Integer keywordId) {
		return dao.selectKeywordByKeywordId(keywordId);
	}
	
	/**
	 * 透過關鍵字，查詢活動資料
	 * 
	 * @param 關鍵字, 頁數
	 * @return 符合條件的數筆活動資料
	 */
	@Transactional
	@Override
	public Core<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
		Core<List<EventInfo>> eventCore = new Core<>();
		Long count = null;
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info
		eventCore.setData(dao.selectEventByKeywordWithPages(keyword, pageNumber, pageSize));
		// 3. 判斷 event_info 資料總筆數
		count = dao.selectEventCountByKeyword(keyword);
		eventCore.setCount(count);
		if (count <= 0) {
			eventCore.setSuccessful(false);
			eventCore.setMessage("查無資料");
		} else {
			eventCore.setSuccessful(true);
			eventCore.setMessage("取得資料");
		}
		return eventCore;
	}

}

package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import common.vo.DataStatus;
import user.buy.dao.SearchDao;
import user.buy.service.SearchService;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;
import user.member.vo.Member;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private SearchDao dao;

	/**
	 * 查詢近期 n 筆活動資料。
	 * 
	 * @param {Integer} n - 指定查詢筆數。
	 * @return {EventInfo} 回應近期活動資料。
	 */
	@Transactional
	@Override
	public List<EventInfo> getRecentEventInfo(Integer n) {
		return dao.selectRecentEventInfo(n);
	}

	/**
	 * 查詢會員的我的關注資料。
	 * 
	 * @param {Member} member - 會員物件。
	 * @return {core<List<Favorite>>} 回應我的關注資料。
	 */
	@Transactional
	@Override
	public Core<List<Favorite>> getFavorite(Member member) {
		var core = new Core<List<Favorite>>();
		var memberId = member.getMemberId();
		
		List<Favorite> favarite = dao.selectFavoriteByMemberId(memberId);
		// 如果查不到資料，回傳空的 List
		if(favarite.isEmpty()) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("沒有任何關注活動");
			core.setSuccessful(false);
			return core;
		}
		// 查到資料，回傳有資料的 List
		core.setDataStatus(DataStatus.FOUND);
		core.setData(favarite);
		core.setMessage("有關注活動");
		core.setSuccessful(true);
		return core;
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

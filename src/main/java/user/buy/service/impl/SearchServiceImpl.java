package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import common.vo.DataStatus;
import common.vo.Order;
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
	 * 查詢活動資料。
	 * 
	 * @param {String}  searchTerm - 輸入關鍵字。
	 * @param {Integer} page - 第幾頁。
	 * @param {Order}   order - 排序方法(DESC/ASC)。
	 * @param {Integer} pageSize - 每頁 item 數量。
	 * @return {Core<List<EventInfo>>} 查詢活動結果。
	 */
	@Transactional
	@Override
	public Core<List<EventInfo>> getEventInfo(String searchTerm, Integer page, Order order, Integer pageSize) {
		var core = new Core<List<EventInfo>>();

		// 參數驗證
		if (page <= 0) {
			core.setDataStatus(DataStatus.INVALID);
			core.setMessage("頁數必須大於 0");
			core.setSuccessful(false);
			return core;
		}

		// 呼叫 dao 層操作查詢
		List<EventInfo> eventList = dao.selectEventInfo(searchTerm, page, order, pageSize);
		Long eventCount = dao.countEventInfo(searchTerm);
		if (eventList.isEmpty()) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("查無資料");
			core.setSuccessful(true);
			core.setData(eventList);
			core.setCount(eventCount);
			core.setPageSize(pageSize);
			return core;
		}
		core.setDataStatus(DataStatus.FOUND);
		core.setMessage("查詢成功");
		core.setSuccessful(true);
		core.setData(eventList);
		core.setCount(eventCount);
		core.setPageSize(pageSize);
		return core;
	}

	/**
	 * 查詢會員的我的關注資料。
	 * 
	 * @param {Member} member - 會員物件。
	 * @return {core<List<Favorite>>} 回應我的關注資料。
	 */
	@Transactional
	@Override
	public Core<List<Favorite>> getAllFavorite(Member member) {
		var core = new Core<List<Favorite>>();
		var memberId = member.getMemberId();

		List<Favorite> favarite = dao.selectAllFavoriteByMemberId(memberId);
		// 如果查不到資料，回傳空的 List
		if (favarite.isEmpty()) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("沒有任何關注活動");
			core.setSuccessful(true);
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
	 * 儲存會員的我的關注資料。
	 * 
	 * @param{Member} member - session.member 會員資料。
	 * @param{Integer} eventId - 活動 id。
	 * @return{Core<Integer>} 儲存資料的識別 id 和操作結果。
	 */
	@Transactional
	@Override
	public Core<Integer> saveFavorite(Member member, Integer eventId) {
		var core = new Core<Integer>();
		var memberId = member.getMemberId();

		// 檢查 eventId 不為0或 < 0
		if (eventId == null || eventId < 0) {
			core.setDataStatus(DataStatus.INVALID);
			core.setMessage("資料不合法");
			core.setSuccessful(false);
			return core;
		}

		// 插入1筆新關注名單
		final var newId = dao.insertFavorite(eventId, memberId);
		if (newId == null || newId <= 0) {
			core.setDataStatus(DataStatus.EXECUTION_FAILED);
			core.setMessage("操作失敗");
			core.setSuccessful(false);
			return core;
		}
		core.setDataStatus(DataStatus.EXECUTION_PASSED);
		core.setMessage("操作成功");
		core.setSuccessful(true);
		core.setData(newId);
		return core;
	}

	/**
	 * 刪除會員的我的關注資料 by (memberId, eventId)。
	 * 
	 * @param{Member} member - session.member 會員資料。
	 * @param{Integer} eventId - 活動 id。
	 * @return{Core<Integer>}
	 */
	@Transactional
	@Override
	public Core<Integer> deleteFavorite(Member member, Integer eventId) {
		var core = new Core<Integer>();
		var memberId = member.getMemberId();

		// 檢查 eventId 不為0或 < 0
		if (eventId == null || eventId < 0) {
			core.setDataStatus(DataStatus.INVALID);
			core.setMessage("資料不合法");
			core.setSuccessful(false);
			return core;
		}

		// 刪除1筆關注名單
		final var deleteCount = dao.removeFavorite(eventId, memberId);
		if (deleteCount == null || deleteCount <= 0) {
			core.setDataStatus(DataStatus.EXECUTION_FAILED);
			core.setMessage("操作失敗");
			core.setSuccessful(false);
			return core;
		}
		core.setDataStatus(DataStatus.EXECUTION_PASSED);
		core.setMessage("操作成功");
		core.setSuccessful(true);
		core.setData(deleteCount);
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
			eventCore.setSuccessful(true);
			eventCore.setMessage("查無資料");
		} else {
			eventCore.setSuccessful(true);
			eventCore.setMessage("取得資料");
		}
		return eventCore;
	}
}

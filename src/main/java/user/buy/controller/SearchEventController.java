package user.buy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import common.vo.Order;
import user.buy.service.SearchService;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.FavoriteDto;
import user.buy.vo.KeywordCategory;
import user.member.vo.Member;

@RestController
@RequestMapping("search-event")
public class SearchEventController {
	@Autowired
	private SearchService service;

	/**
	 * 查詢活動資料。 api 範例： GET
	 * /api/events?keyword={keyword}&page={page}&size={size}&sort={sort}&order={order}
	 * 
	 * @param {String}  keyword - 輸入關鍵字。
	 * @param {Integer} page - 第幾頁。
	 * @param {Order}   order - 排序方法(DESC/ASC)。
	 * @return {List<EventInfo>} 回應活動資料查詢結果(查無資料時，回應空的 List 而非 null)。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping
	public Core<List<EventInfo>> getEventInfo(
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") Integer page, 
			@RequestParam(defaultValue = "ASC") Order order) {
		Integer pageSize = 9; // 強制每頁顯示9筆資料
		return service.getEventInfo(keyword, page, order, pageSize);
	}

	/**
	 * 查詢會員的我的關注資料。
	 * 
	 * @param {Member} member - 會員物件。
	 * @return {Favorite} 某會員的關注資料。
	 */
	@GetMapping("like")
	public Core<List<Favorite>> getAllFavorite(@SessionAttribute(required = false) Member member) {
		var core = new Core<List<Favorite>>();

		if (member == null) {
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}
		return service.getAllFavorite(member);
	}

	/**
	 * 儲存會員的我的關注資料。
	 * 
	 * @param{Member} member - session.member 會員資料。
	 * @param{FavoriteDto} favorite - 關注資料，只含 eventId 這一個唯一欄位。
	 * @return{Core<Integer>} 儲存資料的識別 id 和操作結果。
	 */
	@PostMapping("like")
	public Core<Integer> saveFavorite(@SessionAttribute(required = false) Member member,
			@RequestBody FavoriteDto favorite) {
		var core = new Core<Integer>();

		// 判斷登入與否？
		if (member == null) {
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}

		// 執行儲存關注資料
		return service.saveFavorite(member, favorite.getEventId());
	}

	/**
	 * 移除會員的我的關注資料 by (memberId, eventId)。
	 * 
	 * @param{Member} member - session.member 會員資料。
	 * @param{FavoriteDto} favorite - 關注資料，只含 eventId 這一個唯一欄位。
	 * @return{Core<Integer>} 儲存資料的識別 id 和操作結果。
	 */
	@DeleteMapping("like/{eventId}")
	public Core<Integer> saveFavorite(@SessionAttribute(required = false) Member member,
			@PathVariable Integer eventId) {
		var core = new Core<Integer>();

		// 判斷登入與否？
		if (member == null) {
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}

		// 執行刪除關注資料
		return service.deleteFavorite(member, eventId);
	}

	/**
	 * 透過 keywordId 查詢關鍵字名稱。
	 * 
	 * @param {Integer} keywordId - 關鍵字 id。
	 * @return {KeywordCategory} 回應對應關鍵字資料。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("keyword/{keywordId}")
	public KeywordCategory getKeyword(@PathVariable Integer keywordId) {
		return service.getKeyword(keywordId);
	}
}
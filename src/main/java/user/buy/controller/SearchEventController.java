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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
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
	 * 查詢近期 9 筆活動資料。
	 * 
	 * @return {EventInfo} 回應近期 9 筆活動資料。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("recent")
	public List<EventInfo> getRecentEventInfo() {
		Integer rowToShow = 9; // 9筆近期
		return service.getRecentEventInfo(rowToShow);
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
package user.buy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.buy.service.SearchService;
import user.buy.vo.EventInfo;
import user.buy.vo.KeywordCategory;

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
		Integer rowNumber = 9; // 9筆近期
		return service.getRecentEventInfo(rowNumber);
	}
	
	/**
	 * 透過 keywordId 查詢關鍵字名稱。
	 * 
	 * @return {KeywordCategory} 回應對應關鍵字資料。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("keyword/{keywordId}")
	public KeywordCategory getKeyword(@PathVariable Integer keywordId) {
		return service.getKeyword(keywordId);
	}
}
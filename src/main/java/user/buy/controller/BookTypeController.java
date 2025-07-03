package user.buy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import user.buy.service.BookService;
import user.buy.vo.BookDto;
import user.buy.vo.BookTypeDto;
import user.buy.vo.EventInfo;
import user.member.vo.Member;

@RestController
@RequestMapping("book-type")
public class BookTypeController {
	@Autowired
	private BookService service;

	/**
	 * 透過活動 id 查詢票種資訊
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookTypeDto>} 活動 id 下的票種資訊。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("event/{eventId}/event-ticket-type")
	public List<BookTypeDto> getTicketType(@PathVariable Integer eventId) {
		return service.getTypeById(eventId);
	}

	/**
	 * 透過活動 id 查詢活動資訊
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookTypeDto} 活動 id 下的活動資訊。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("event/{eventId}")
	public EventInfo getTicketEvent(@PathVariable Integer eventId) {
		return service.getEventById(eventId);
	}

	/**
	 * 將訂單資訊(票種選擇)儲存至 Redis。
	 * 
	 * @param {BookDto} book - 票種選擇結果資料。
	 * @param {Member}  member - Session 的會員物件。
	 * @return {Core<String>} 回應操作結果。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> saveBookType(@RequestBody BookDto book, @SessionAttribute(required = false) Member member) {
		Core<String> core = new Core<>();

		// 如果沒登入，則回傳錯誤訊息
		if (member == null) {
			core.setMessage("請先登入");
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setSuccessful(false);
			return core;
		}

		// 將 userName 存入 book 物件
		book.setUserName(member.getUserName());
		// 存入 book 物件至 Redis 並設定 TTL 15分鐘
		return service.saveBookType(book, 15);
	}
}

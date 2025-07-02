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
@RequestMapping("book-confirm")
public class BookConfirmController {
	@Autowired
	private BookService service;

	/**
	 * 透過 活動 id 取得活動資訊。
	 * 
	 * @param {Integer} eventId - 活動識別 id。
	 * @return {BookEventDto} 回應活動資訊查詢結果。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("event/{eventId}")
	public EventInfo getEventInfo(@PathVariable Integer eventId) {
		return service.getEventById(eventId);
	}

	/**
	 * 透過 活動 id 查詢票種資訊。
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
	 * 從 Redis 取得訂單選擇結果資料。 (票種 + 個人資料)
	 * 
	 * @param {Member} member - Session 的會員物件。
	 * @return {Core<BookDto>} 回應儲存資料(共用)。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping
	public Core<BookDto> getBook(@SessionAttribute(required = false) Member member) {
		// 若還未登入...
		if (member == null) {
			Core<BookDto> core = new Core<>();
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}

		// 登入後...
		return service.getBook(member.getUserName());
	}

	/**
	 * 將訂單資訊(票種 + 個人資料)儲存至 Redis。
	 * 
	 * @param {BookDto} book - 訂單資料。 (不設定 TTL (分鐘))
	 * @param {Member}  member - Session 的會員物件。
	 * @return {Core<String>} 回應操作結果。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> saveBookConfirm(@RequestBody BookDto book, @SessionAttribute(required = false) Member member) {
		var core = new Core<String>();

		// 如果沒登入，則回傳錯誤訊息
		if (member == null) {
			core.setMessage("請先登入");
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setSuccessful(false);
			return core;
		}

		// 存入 book 物件至 Redis(不再更新 TTL)
		return service.saveBookConfirm(book);
	}

	/**
	 * 將訂單資訊(票種 + 個人資料)儲存至資料庫。
	 * 
	 * @param {BookDto} book - 個人資料填寫結果(含先前的)。
	 * @param {Member}  member - Session 的會員物件。
	 * @return {Core<String>} 回應操作結果。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping("save")
	public Core<String> saveOrderAndTicket(@RequestBody BookDto book,
			@SessionAttribute(required = false) Member member) {
		var core = new Core<String>();

		// 如果沒登入，則回傳錯誤訊息
		if (member == null) {
			core.setMessage("請先登入");
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setSuccessful(false);
			return core;
		}

		// 存入 book 物件至資料庫中(從 Redis 移轉至 mySQL)
		return service.saveOrderAndTicket(book);
	}
}

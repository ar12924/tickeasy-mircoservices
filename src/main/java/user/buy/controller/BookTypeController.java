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

import common.vo.Core;
import user.buy.service.BookService;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;
import user.buy.vo.BookDto;

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
	public BookEventDto getTicketEvent(@PathVariable Integer eventId) {
		return service.getEventById(eventId);
	}

	/**
	 * 將購票頁資訊(book)，暫存到 Redis 中。
	 * 
	 * @param {BookDto} book - 購票頁資料。
	 * @param {Member} member - Session 的會員物件。
	 * @return {Core<String>} 儲存操作結果，沒登入回傳錯誤、已登入回傳成功。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> saveBook(@RequestBody BookDto book, @SessionAttribute(required = false) Member member) {
		Core<String> core = new Core<>();

		// 如果沒登入，則回傳錯誤訊息
		if (member == null) {
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		} else {
			// 將 eventId, member 存入 book 物件
			book.setUserName(member.getUserName());
			// book 物件存入 Redis，並設定 TTL 15分鐘
			service.saveBook(book, 15);
			// 回應成功訊息
			core.setMessage("購票頁資訊存至 Redis");
			core.setSuccessful(true);
			return core;
		}
	}
}

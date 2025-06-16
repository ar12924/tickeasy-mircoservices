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

import common.vo.Core;
import user.buy.service.BookService;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
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
	 * 將票券訂購資訊，暫存到 Redis 中
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookTypeDto} 活動 id 下的活動資訊。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> saveBook(@RequestBody BookDto book) {
		int memberId = 5; // 預計由 session 物件取得
		// 1. 包裝 url 參數, session 屬性及請求本體
		book.setMemberId(memberId);
		System.out.println(book.toString());
		return null;
	}
}

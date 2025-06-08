package user.buy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import common.vo.Core;
import user.buy.service.BookService;
import user.buy.vo.TempBook;

@Controller
@RequestMapping("user/buy")
public class BookTicketsController {
	@Autowired
	private BookService service;

	@CrossOrigin(origins = "*")
	@GetMapping("book-tickets")
	@ResponseBody
	public List<Object[]> bookTickets(@RequestParam("eventId") int eventId) {
		return service.findTypeAndEventById(eventId);
	}

	@CrossOrigin(origins = "*")
	@PostMapping("book-tickets")
	@ResponseBody
	public Core<String> bookTicketsSave(@RequestParam int eventId, @RequestBody TempBook tempBook) {
		int memberId = 5; // 預計由 session 物件取得
		// 1. 包裝 url 參數, session 屬性及請求物件
		tempBook.setMemberId(memberId);
		// 2. 訂單快取至 Redis
		return service.saveBook(tempBook);
	}
}

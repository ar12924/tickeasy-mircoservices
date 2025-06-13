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
import user.buy.vo.BookTypeInfoDto;
import user.buy.vo.TempBook;

@RestController
@RequestMapping("buy/book-type")
public class BookTypeController {
	@Autowired
	private BookService service;

	@CrossOrigin(origins = "*")
	@GetMapping("{eventId}")
	public List<BookTypeInfoDto> getTypeAndEvent(@PathVariable Integer eventId) {
		// 1. 查詢 type + event
		return service.findTypeAndEventById(eventId);
	}

	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> save(@RequestBody TempBook tempBook) {
		int memberId = 5; // 預計由 session 物件取得
		// 1. 包裝 url 參數, session 屬性及請求本體
		tempBook.setMemberId(memberId);
		return service.saveBook(tempBook);
	}
}

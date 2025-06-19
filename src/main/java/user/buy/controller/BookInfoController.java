package user.buy.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.Core;
import user.buy.service.BookService;
import user.member.vo.Member;
import user.buy.vo.BookDto;

@RestController
@RequestMapping("book-info")
public class BookInfoController {
	@Autowired
	private BookService service;

	/**
	 * 從 Redis 中，取得購票頁資訊(book)。
	 * 
	 * @param {Member} member - Session 的會員物件。
	 * @return {Core<BookDto>} book 查詢結果。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping
	public Core<BookDto> getBook(@SessionAttribute(required = false) Member member) {
		Core<BookDto> core = new Core<>();
		
		// 若還未登入...
		if (member == null) {
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}
		
		// 登入後，查詢結果
		List<BookDto> dataList = new ArrayList<>();
		dataList.add(service.getBook(member.getUserName()));
		core.setData(dataList);
		core.setMessage("成功查詢購票頁資訊");
		core.setSuccessful(true);
		return core;
	}
	
	/**
	 * 透過 userName 查詢購票人資訊(member)。
	 * 
	 * @param {String} userName - 購票人 userName。
	 * @return {Member} member 購票人資訊。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("member/{userName}")
	public Member getMember(@PathVariable String userName) {
		return service.getMember(userName);
	}
}

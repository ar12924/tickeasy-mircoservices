package user.buy.controller;

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
import user.member.vo.Member;

@RestController
@RequestMapping("book-info")
public class BookInfoController {
	@Autowired
	private BookService service;

	/**
	 * 從 Redis 取得票種選擇結果資料。 或 從 Redis 取得個人資料填寫結果資料。
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

	/**
	 * 將訂單資訊(個人資料填寫)儲存至 Redis。
	 * 
	 * @param {BookDto} book - 個人資料填寫結果(含先前的)。
	 * @param {Member}  member - Session 的會員物件。
	 * @return {Core<String>} 回應操作結果。 (不重新設定 TTL(分鐘))
	 */
	@CrossOrigin(origins = "*")
	@PostMapping
	public Core<String> saveBookInfo(@RequestBody BookDto book, @SessionAttribute(required = false) Member member) {
		var core = new Core<String>();

		// 如果沒登入，則回傳錯誤訊息
		if (member == null) {
			core.setMessage("請先登入");
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setSuccessful(false);
			return core;
		}

		// 存入 book 物件至 Redis(不再更新 TTL)
		return service.saveBookInfo(book);
	}

	/**
	 * 驗證入場者資料是否存在。
	 * 
	 * @param {BookDto} book - 個人資料填寫結果(book)。
	 * 
	 *                  <pre>
	 *  請求本體範例(book):
	 * {
	 *     "eventId": "1",
	 *     // ...
	 *     "attendee": [
	 *         {
	 *             "userName": "buyer2",
	 *             "idCard": "F123456789",
	 *         },
	 *         // ...
	 *     ]
	 * }
	 *                  </pre>
	 * 
	 * @return {Core<String>} 入場者資料驗證結果。
	 */
	@CrossOrigin(origins = "*")
	@PostMapping("member/verify")
	public Core<String> verifyMemberIdCard(@RequestBody Member reqMember) {
		return service.verifyMemberIdCard(reqMember);
	}
}

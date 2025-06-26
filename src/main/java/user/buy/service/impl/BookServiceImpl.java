package user.buy.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import common.vo.Core;
import common.vo.DataStatus;
import user.buy.dao.BookDao;
import user.buy.service.BookService;
import user.buy.vo.BookDto;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookDao dao;
	@Autowired
	private RedisTemplate<String, Object> template;

	/**
	 * 透過活動 id，查詢票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookInfoDto>} 活動 id 下的票種資訊。
	 */
	@Transactional
	@Override
	public List<BookTypeDto> getTypeById(Integer eventId) {
		return dao.selectTypeById(eventId);
	}

	/**
	 * 透過活動 id，查詢活動資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookEventDto} 活動 id 下的票種資訊。
	 */
	@Transactional
	@Override
	public BookEventDto getEventById(Integer eventId) {
		return dao.selectEventById(eventId);
	}

	/**
	 * 將票種選擇結果儲存至 Redis。
	 * 
	 * @param {BookDto} book - 票種選擇資料。
	 * @param {long}    timeoutMinutes - 設定 TTL (分鐘)。
	 */
	@Override
	public Core<String> saveBookType(BookDto book, long timeoutMinutes) {
		var core = new Core<String>();

		// 如果 eventId 或 userName 缺少其中1個
		if (book.getEventId() <= 0 || !StringUtils.hasText(book.getUserName())) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("資料有缺漏，請重新選擇票種");
			core.setSuccessful(false);
			return core;
		}

		// 如果 book 的 quantity 總和小於 0
		var selectedList = book.getSelected();
		Integer total = selectedList.stream()
				.mapToInt(ticketType -> ticketType.getQuantity() == null ? 0 : ticketType.getQuantity()).sum();
		if (total <= 0) {
			core.setDataStatus(DataStatus.INVALID);
			core.setMessage("請至少選擇1張票券");
			core.setSuccessful(false);
			return core;
		}

		// 以 key = userName 將 book 存入 Redis 當中
		template.opsForValue().set(book.getUserName(), book, timeoutMinutes, TimeUnit.MINUTES);
		core.setDataStatus(DataStatus.VALID);
		core.setMessage("選擇票種儲存成功");
		core.setSuccessful(true);
		return core;
	}

	/**
	 * 將訂單資訊(個人資料填寫)儲存至 Redis。
	 * 
	 * @param {BookDto} book - 個人資料。 (不設定 TTL (分鐘))
	 */
	@Override
	public Core<String> saveBookInfo(BookDto book) {
		var core = new Core<String>();

		// 如果 eventId 或 userName 缺少其中1個
		if (book.getEventId() <= 0 || !StringUtils.hasText(book.getUserName())) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("資料有缺漏，請重新選擇票種");
			core.setSuccessful(false);
			return core;
		}

		// 以 key = userName 將 book 存入 Redis 當中
		template.opsForValue().set(book.getUserName(), book);
		core.setDataStatus(DataStatus.FOUND);
		core.setMessage("填寫資料儲存成功");
		core.setSuccessful(true);
		return core;
	}

	/**
	 * 從 Redis 取得票種選擇結果資料。<br>
	 * 或<br>
	 * 從 Redis 取得個人資料填寫結果資料。
	 * 
	 * @param {String} userName - 會員名。
	 * @return {Core<BookDto>} 回應儲存 Redis 資料。
	 */
	@Override
	public Core<BookDto> getBook(String userName) {
		Core<BookDto> core = new Core<>();
		// 以 key = userName 從 Redis 中查詢 book 物件
		var bookDto = (BookDto) template.opsForValue().get(userName);

		// 檢查有無資料(因 Redis TTL 時間到會移除)
		if (bookDto == null) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("無任何購票資料，請先選擇票種!!");
			core.setSuccessful(false);
			return core;
		}

		// 確保資料中的 userName 與當前會員為同一人
		if (!Objects.equals(bookDto.getUserName(), userName)) {
			core.setDataStatus(DataStatus.FORBIDDEN);
			core.setMessage("資料存取權限不足");
			core.setSuccessful(false);
			return core;
		}

		// 資料正常取得...
		core.setData(bookDto);
		core.setDataStatus(DataStatus.FOUND);
		core.setMessage("取得訂購資料");
		core.setSuccessful(true);
		return core;
	}

	/**
	 * 透過 userName 查詢會員資料(當前購票人)。
	 * 
	 * @param {String} userName - 會員 userName。
	 * @return {Member} member 會員資料。
	 */
	@Override
	public Member getMember(String userName) {
		return dao.selectMemberByUserName(userName);
	}

	/**
	 * 依據票券個人資料中的帳號，驗證會員身分證字號的正確性。
	 * 
	 * @param {Member} reqMember - 入場者個人資料。
	 * @return {Core<String>} 驗證結果訊息(成功或失敗)。
	 */
	@Override
	public Core<String> verifyMemberIdCard(Member reqMember) {
		var memberCore = new Core<String>();

		// 身分證字號不得為空
		var reqIdCard = reqMember.getIdCard();
		if (!StringUtils.hasText(reqIdCard)) {
			memberCore.setDataStatus(DataStatus.NOT_FOUND);
			memberCore.setMessage("身分證號為空");
			memberCore.setSuccessful(false);
			return memberCore;
		}

		// 身分證字號格式驗證
		var isIdCard = reqIdCard.matches("^[A-Z][0-9]{9}$");
		if (!isIdCard) {
			memberCore.setDataStatus(DataStatus.INVALID);
			memberCore.setMessage("身分證號格式錯誤");
			memberCore.setSuccessful(false);
			return memberCore;
		}

		// 查詢會員資料，比對 idCard 與請求本體是否一致
		Member member = dao.selectMemberByUserName(reqMember.getUserName());

		// 如果查詢會員資料不存在...
		if (member == null) {
			memberCore.setDataStatus(DataStatus.NOT_FOUND);
			memberCore.setMessage("會員不存在");
			memberCore.setSuccessful(false);
			return memberCore;
		}

		// 比對後不一致
		var idCard = member.getIdCard();
		if (!Objects.equals(reqIdCard, idCard)) {
			memberCore.setDataStatus(DataStatus.COMPARISON_FAILED);
			memberCore.setMessage("身份驗證失敗");
			memberCore.setSuccessful(false);
			return memberCore;
		}

		// 比對後一致
		memberCore.setDataStatus(DataStatus.COMPARISON_PASSED);
		memberCore.setMessage("身份驗證成功");
		memberCore.setSuccessful(true);
		return memberCore;
	}
}

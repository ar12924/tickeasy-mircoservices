package user.buy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import user.buy.dao.BookDao;
import user.buy.service.BookService;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;
import user.buy.vo.BookDto;

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
	 * 將購票頁資訊(book)，暫存到 Redis 中。 若 Redis 寫入失敗，會拋出 RuntimeException。
	 * 
	 * @param {BookDto} book - 購票頁資料。
	 * @param {long}    timeoutMinutes - 設定 TTL (分鐘)。
	 */
	@Override
	public void saveBook(BookDto book, long timeoutMinutes) {
		// 以 key = userName 將 book 存入 Redis 當中
		template.opsForValue().set(book.getUserName(), book, timeoutMinutes, TimeUnit.MINUTES);
	}

	/**
	 * 從 Redis 中，取得購票頁資訊(book)。
	 * 
	 * @param {String} userName - 會員名稱。
	 */
	@Override
	public BookDto getBook(String userName) {
		// 以 key = userName 從 Redis 中查詢 book 物件
		return (BookDto) template.opsForValue().get(userName);
	}

	/**
	 * 透過 userName 查詢購票人資訊(member)。
	 * 
	 * @param {String} userName - 購票人 userName。
	 * @return {Member} member 購票人資訊。
	 */
	@Override
	public Member getMember(String userName) {
		return dao.selectMemberByUserName(userName);
	}
	
	/**
	 * 透過 userName 驗證購票人是否存在。
	 * 
	 * @param {String} userName - 購票人 userName。
	 * @return {Core<Member>} 存在驗證成功，反之則失敗。
	 */
	@Override
	public Core<Member> verifyMemberByUserName(String userName) {
		Core<Member> memberCore = new Core<>();

		Member member = dao.selectMemberByUserName(userName);
		// 若查無資料 (Member == null)
		if (member == null) {
			memberCore.setMessage("此帳號尚未註冊");
			memberCore.setSuccessful(false);
			return memberCore;
		}
		
		// 有查到資料 (Member != null)
		List<Member> memberList = new ArrayList<>();
		memberList.add(member);
		memberCore.setData(memberList);
		memberCore.setMessage("帳號驗證成功");
		memberCore.setSuccessful(true);
		return memberCore;
	}
	
	/**
	 * 透過 idCard 驗證購票人是否存在。
	 * 
	 * @param {String} idCard - 購票人 idCard。
	 * @return {Core<Member>} 存在驗證成功，反之則失敗。
	 */
	@Override
	public Core<Member> verifyMemberByIdCard(String idCard) {
		Core<Member> memberCore = new Core<>();

		Member member = dao.selectMemberByIdCard(idCard);
		// 若查無資料 (Member == null)
		if (member == null) {
			memberCore.setMessage("身分證字號驗證失敗");
			memberCore.setSuccessful(false);
			return memberCore;
		}
		
		// 有查到資料 (Member != null)
		List<Member> memberList = new ArrayList<>();
		memberList.add(member);
		memberCore.setData(memberList);
		memberCore.setMessage("身分證字號驗證成功");
		memberCore.setSuccessful(true);
		return memberCore;
	}
}

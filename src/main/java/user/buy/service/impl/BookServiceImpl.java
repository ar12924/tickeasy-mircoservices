package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import user.buy.dao.BookDao;
import user.buy.service.BookService;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.buy.vo.BookDto;
import user.buy.vo.Selected;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	private BookDao dao;
	@Autowired
	private RedisTemplate<String, Object> template;

	@Transactional
	@Override
	public List<BookTypeDto> getTypeById(Integer eventId) {
		return dao.selectTypeById(eventId);
	}

	@Transactional
	@Override
	public BookEventDto getEventById(Integer eventId) {
		return dao.selectEventById(eventId);
	}

	@Override
	public Core<String> saveBook(BookDto book) {
		HashOperations<String, Object, Object> hashOps = template.opsForHash();
		Core<String> message = new Core<>();

		// 1. 若 memberId 沒有抓到，則不進行訂單暫存
		if (book.getMemberId() <= 0) {
			message.setMessage("無會員資料，訂單暫存失敗!!");
			message.setSuccessful(false);
			return message;
		}

		// 2. 訂單基本資料
		// hash 型態的 key 為 tempBook:{memberId}:{eventId}
		String key = "tempBook:" + book.getMemberId() + ":" + book.getEventId();
		String innerKey = key + ":selections";
		hashOps.put(key, "memberId", book.getMemberId());
		hashOps.put(key, "eventId", book.getEventId());
		hashOps.put(key, "eventName", book.getEventName());
		hashOps.put(key, "selected", innerKey); // 關聯 -> selections[]

		// 3. selections: 訂單的 "票券" 資料
		// hash 型態的 key 為 tempBook:{memberId}:{eventId}:selections:{index}
		for (int i = 0; i < book.getSelected().size(); i++) {
			key = innerKey + ":" + (i + 1);
			Selected selection = book.getSelected().get(i);
			hashOps.put(key, "typeId", selection.getTypeId());
			hashOps.put(key, "quantity", selection.getQuantity());
			hashOps.put(key, "categoryName", selection.getCategoryName());
		}

		// 4. 回傳儲存成功訊息
		message.setMessage("訂單票券已暫存!!");
		message.setSuccessful(true);
		return message;
	}
}

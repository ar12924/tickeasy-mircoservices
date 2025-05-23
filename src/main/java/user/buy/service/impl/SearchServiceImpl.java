package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Payload;
import user.buy.dao.SearchDao;
import user.buy.dao.impl.SearchDaoImpl;
import user.buy.service.SearchService;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private SearchDao buyDaoImpl;

//	public SearchServiceImpl() {
//		buyDaoImpl = new SearchDaoImpl();
//	}

	@Transactional
	@Override
	public Payload<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
		Payload<List<EventInfo>> eventPayload = null;
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info(事務開始)
		eventPayload = buyDaoImpl.selectEventByKeyword(keyword, pageNumber, pageSize);
		// 3. 判斷回傳資料是否為空的？
		if (eventPayload.getData().isEmpty()) {
			eventPayload.setSuccessful(false);
			eventPayload.setMessage("查無資料");
		} else {
			eventPayload.setSuccessful(true);
			eventPayload.setMessage("取得資料");
		}
		return eventPayload;
	}

	@Transactional
	@Override
	public List<BuyerTicket> searchTicket() {
		// 1. 查詢 buyer_ticket
		return buyDaoImpl.selectTicket();
	}

	@Transactional
	@Override
	public List<MemberNotification> searchNotification() {
		// 1. 查詢 member_notification
		return buyDaoImpl.selectNotification();
	}

}

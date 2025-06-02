package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Payload;
import user.buy.dao.SearchDao;
import user.buy.service.SearchService;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	private SearchDao buyDaoImpl;

	@Transactional
	@Override
	public Payload<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
		Payload<List<EventInfo>> eventPayload = new Payload<>();
		Long count = null;
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info
		eventPayload.setData(buyDaoImpl.selectEventByKeywordWithPages(keyword, pageNumber, pageSize));
		// 3. 判斷 event_info 資料總筆數
		count = buyDaoImpl.selectEventCountByKeyword(keyword);
		eventPayload.setCount(count);
		if (count <= 0) {
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

package user.buy.service.impl;

import java.util.List;

import common.vo.Payload;
import user.buy.dao.SearchDao;
import user.buy.dao.impl.SearchDaoImpl;
import user.buy.service.SearchService;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class SearchServiceImpl implements SearchService {
	private SearchDao buyDaoImpl;

	public SearchServiceImpl() {
		buyDaoImpl = new SearchDaoImpl();
	}

	@Override
	public Payload<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize) {
		Payload<List<EventInfo>> eventPayload = new Payload<>();
		Long count = null;
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info(事務開始)
		eventPayload.setData(buyDaoImpl.selectEventByKeywordWithPages(keyword, pageNumber, pageSize));
		// 3. 判斷回傳資料總筆數
		count = buyDaoImpl.selectEventCountByKeyword(keyword);
		eventPayload.setCount(count);
		if(count <= 0) {
			eventPayload.setSuccessful(false);
			eventPayload.setMessage("查無資料");
		}else {
			eventPayload.setSuccessful(true);
			eventPayload.setMessage("取得資料");
		}
		return eventPayload;
	}

	@Override
	public List<BuyerTicket> searchTicket() {
		// 1. 查詢 buyer_ticket
		List<BuyerTicket> ticketList = buyDaoImpl.selectTicket();
		return ticketList;
	}

	@Override
	public List<MemberNotification> searchNotification() {
		// 1. 查詢 member_notification
		List<MemberNotification> memberNotifLst = buyDaoImpl.selectNotification();
		return memberNotifLst;
	}
}

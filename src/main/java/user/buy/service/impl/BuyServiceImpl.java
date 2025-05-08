package user.buy.service.impl;

import java.util.List;

import user.buy.dao.BuyDao;
import user.buy.dao.impl.BuyDaoImpl;
import user.buy.service.BuyService;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class BuyServiceImpl implements BuyService {
	private BuyDao buyDaoImpl;

	public BuyServiceImpl() {
		buyDaoImpl = new BuyDaoImpl();
	}

	@Override
	public List<EventInfo> searchEventByKeyword(String keyword) {
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info
		return buyDaoImpl.selectEventByKeyword(keyword);
	}

	@Override
	public List<BuyerTicket> searchTicket() {
		// 1. 查詢 buyer_ticket
		return buyDaoImpl.selectTicket();
	}

	@Override
	public List<MemberNotification> searchNotification() {
		// 1. 查詢 member_notification
		return buyDaoImpl.selectNotification();
	}

}

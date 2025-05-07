package user.buy.service.impl;

import java.util.List;

import user.buy.dao.BuyDao;
import user.buy.dao.impl.BuyDaoImpl;
import user.buy.service.BuyService;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;

public class BuyServiceImpl implements BuyService {
	private BuyDao buyDaoImpl;

	public BuyServiceImpl() {
		buyDaoImpl = new BuyDaoImpl();
	}

	@Override
	public List<EventInfo> searchEventByKeyword(String keywords) {
		// 1. 過濾 keywords
		keywords = keywords == null ? "" : keywords;
		// 2. 查詢 event_info
		return buyDaoImpl.selectEventByKeyword(keywords);
	}

	@Override
	public List<BuyerTicket> searchTicket() {
		// 1. 查詢 buyer_ticket
		return buyDaoImpl.selectTicket();
	}

}

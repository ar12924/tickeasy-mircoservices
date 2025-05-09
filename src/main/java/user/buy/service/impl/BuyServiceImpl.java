package user.buy.service.impl;

import java.util.List;

import common.vo.Payload;
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
	public Payload<List<EventInfo>> searchEventByKeyword(String keyword) {
		// 1. 過濾 keywords
		keyword = keyword == null ? "" : keyword;
		// 2. 查詢 event_info
		List<EventInfo> eventInfoLst =  buyDaoImpl.selectEventByKeyword(keyword);
		// 3. 判斷回傳資料是否為空的？
		Payload<List<EventInfo>> payload = new Payload<>();
		if(eventInfoLst.isEmpty()) {
			payload.setSuccessful(false);
			payload.setMessage("查無資料");
		}else {
			payload.setSuccessful(true);
			payload.setMessage("取得資料");
		}
		payload.setData(eventInfoLst);
		return payload;
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

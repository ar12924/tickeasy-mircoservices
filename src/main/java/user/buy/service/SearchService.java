package user.buy.service;

import java.util.List;

import common.service.CommonService;
import common.vo.Payload;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public interface SearchService extends CommonService{
	Payload<List<EventInfo>> searchEventByKeyword(String keywords);

	List<BuyerTicket> searchTicket();
	
	List<MemberNotification> searchNotification();
}

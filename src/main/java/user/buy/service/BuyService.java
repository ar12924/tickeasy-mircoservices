package user.buy.service;

import java.util.List;

import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;

public interface BuyService {
	List<EventInfo> searchEventByKeyword(String keywords);

	List<BuyerTicket> searchTicket();
}

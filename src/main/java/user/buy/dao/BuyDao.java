package user.buy.dao;

import java.util.List;

import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public interface BuyDao {
	List<EventInfo> selectEventByKeyword(String keywords);

	List<BuyerTicket> selectTicket();
	
	List<MemberNotification> selectNotification();
}

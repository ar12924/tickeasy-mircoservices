package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public interface SearchDao extends CommonDao {
	List<EventInfo> selectEventByKeyword(String keywords);

	List<BuyerTicket> selectTicket();
	
	List<MemberNotification> selectNotification();
}

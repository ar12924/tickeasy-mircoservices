package user.buy.dao;

import java.util.List;
import user.buy.vo.EventInfo;

public interface BuyDao {
	List<EventInfo> selectEventByKeyword(String keywords);
}

package user.buy.dao;

import java.util.List;

public interface BookDao {
	/**
	 * 透過活動 id，查詢 "票種" + "活動資訊"。
	 * 
	 * @param {int} eventId - 活動 id。
	 * @return {List<Object[]>} 活動 id 下的 "票種" + "活動資訊"。
	 */
	List<Object[]> selectTypeJoinEventById(int eventId);
}

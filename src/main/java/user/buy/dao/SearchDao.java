package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public interface SearchDao extends CommonDao {
	/**
	 * 傳入關鍵字、頁數，查詢活動資料表
	 * 
	 * @param 關鍵字, 頁數, 每頁筆數
	 * @return 符合條件的數筆活動資料
	 */
	List<EventInfo> selectEventByKeywordWithPages(String keyword, Integer pageNumber, Integer pageSize);

	/**
	 * 傳入關鍵字查詢活動資料表
	 * 
	 * @param 關鍵字
	 * @return 活動資料表總筆數
	 */
	public Long selectEventCountByKeyword(String keyword);
	
	/**
	 * 查詢票券資料表
	 * 
	 * @param 無
	 * @return 數筆票券資料
	 */
	List<BuyerTicket> selectTicket();

	/**
	 * 查詢通知資料表
	 * 
	 * @param 無
	 * @return 數筆通知資料
	 */
	List<MemberNotification> selectNotification();
}

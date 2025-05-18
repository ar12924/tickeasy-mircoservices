package user.buy.service;

import java.util.List;

import common.service.CommonService;
import common.vo.Payload;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public interface SearchService extends CommonService {
	/**
	 * 傳入關鍵字，驗證使用者輸入的關鍵字格式
	 * 
	 * @param 關鍵字, 頁數, 每頁筆數
	 * @return 符合條件的數筆活動資料
	 */
	Payload<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize);

	/**
	 * ...
	 * 
	 * @param 無
	 * @return 符合條件的數筆票券資料
	 */
	List<BuyerTicket> searchTicket();

	/**
	 * ...
	 * 
	 * @param 無
	 * @return 符合條件的數筆通知資料
	 */
	List<MemberNotification> searchNotification();
}

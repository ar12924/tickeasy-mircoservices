package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.TempBook;

public interface BookService {
	/**
	 * 透過活動 id，查詢 "票種" + "活動資訊"。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<Object[]>} 活動 id 下的 "票種" + "活動資訊"。
	 */
	List<Object[]> findTypeAndEventById(int eventId);

    /**
     * 接收訂單資料，向 redis 暫存
     *
     * @param 訂單資料
     */
    Core<String> saveBook(TempBook tempBook);
}

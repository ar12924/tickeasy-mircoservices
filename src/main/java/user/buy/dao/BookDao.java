package user.buy.dao;

import java.util.List;

import user.buy.vo.BookTypeInfoDto;

public interface BookDao {
	/**
	 * 透過活動 id，查詢 "票種" + "活動資訊"。
	 * 
	 * @param {int} eventId - 活動 id。
	 * @return {BookInfoDto} 活動 id 下的 "票種" + "活動資訊"。
	 */
	List<BookTypeInfoDto> selectTypeJoinEventById(Integer eventId);
}

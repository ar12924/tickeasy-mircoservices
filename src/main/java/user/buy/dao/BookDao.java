package user.buy.dao;

import java.util.List;

import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;

public interface BookDao {
	/**
	 * 透過活動 id，查詢票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookTypeDto>} 活動 id 下的票種資訊。
	 */
	List<BookTypeDto> selectTypeById(Integer eventId);

	/**
	 * 透過活動 id，查詢活動資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookEventDto} 活動 id 下的活動資訊。
	 */
	BookEventDto selectEventById(Integer eventId);
}

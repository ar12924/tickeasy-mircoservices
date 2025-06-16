package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.buy.vo.TempBook;

public interface BookService {
	/**
	 * 透過活動 id，查詢票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookInfoDto>} 活動 id 下的票種資訊。
	 */
	List<BookTypeDto> getTypeById(Integer eventId);

	/**
	 * 透過活動 id，查詢活動資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookEventDto} 活動 id 下的票種資訊。
	 */
	BookEventDto getEventById(Integer eventId);
	
    /**
     * 接收訂單資料，向 redis 暫存
     *
     * @param {TempBook} tempbook - 訂單資料。
     * @return {Core<String>} 訂單儲存操作結果。
     */
    Core<String> saveBook(TempBook tempBook);
}

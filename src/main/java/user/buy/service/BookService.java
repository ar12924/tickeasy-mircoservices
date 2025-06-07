package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.TempBook;
import user.buy.vo.EventTicketType;

public interface BookService {
    /**
     * 選定某活動，查詢所有票種
     *
     * @param 活動 id
     * @return 符合條件的票種資料
     */
    List<EventTicketType> findTicketType(Integer eventId);

    /**
     * 接收訂單資料，向 redis 暫存
     *
     * @param 訂單資料
     */
    Core<String> saveBook(TempBook tempBook);
}

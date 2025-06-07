package user.buy.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import user.buy.service.BookService;
import user.buy.vo.TempBook;
import user.buy.vo.TempSelection;
import user.buy.vo.EventTicketType;

import java.util.List;

@Controller
@RequestMapping("user/buy")
public class BookTicketsController {
    @Autowired
    private BookService service;

    @CrossOrigin(origins = "*")
    @GetMapping("book-tickets")
    @ResponseBody
    public List<EventTicketType> bookTickets(@RequestParam("eventId") int eventId) {
        return service.findTicketType(eventId);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("book-tickets/save")
    @ResponseBody
    public Core<String> bookTicketsSave(@RequestParam int eventId, @RequestBody List<TempSelection> selectionLst){
        int memberId = 5; // 預計由 session 物件取得
        String eventName = "2024 春季搖滾季"; // 預計由前端 js 取得
        // 1. 包裝 url 參數, session 屬性及請求物件
        var tempBook = new TempBook();
        tempBook.setMemberId(memberId);
        tempBook.setEventId(eventId);
        tempBook.setEventName(eventName);
        tempBook.setSelections(selectionLst);
        // 2. 訂單快取至 Redis
        return service.saveBook(tempBook);
    }
}

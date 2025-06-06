package user.buy.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.util.CommonUtil;
import common.vo.Core;
import user.buy.service.BookService;
import user.buy.vo.TempBook;
import user.buy.vo.TempSelection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 購票頁(bookTickets.html)下一步資料存入 redis 請求方法: POST 回應格式: text/plain (確認成功與否的回應訊息)
 * 資料型態: List<TicketType> API: /user/buy/book-tickets/save
 */
@WebServlet("/user/buy/book-tickets/save")
public class BookTicketsSaveController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BookService service;

	// DL 方式注入(暫時性，後續 spring-MVC 會改)
	@Override
	public void init() throws ServletException {
		service = CommonUtil.getBean(getServletContext(), BookService.class);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 承接 json 格式資料 (BookTicket 的陣列)
		Gson gson = new Gson();
		int memberId = 5; // 先寫死，預計由 session 物件取得
		int eventId = 1; // 先寫死，預計由 req.getParameter 取得(URL後方參數)
		String eventName = "2024 春季搖滾季"; // 先寫死，預計由前端 js 取得
		TempBook tempBook = new TempBook();
		tempBook.setMemberId(memberId);
		tempBook.setEventId(eventId);
		tempBook.setEventName(eventName);
		// 3. 使用 TypeToken 來獲取 List<BookTicket> 的 Type 資訊
		Type bookTicketLstType = new TypeToken<List<TempSelection>>() {
		}.getType();
		tempBook.setSelections(gson.fromJson(req.getReader(), bookTicketLstType));
		// 4. 訂單資訊快取到 Redis
		Core<String> message = service.cacheBook(tempBook);
		// 5. 回應 json 字串
		String jsonMessage = gson.toJson(message);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonMessage);
	}
}

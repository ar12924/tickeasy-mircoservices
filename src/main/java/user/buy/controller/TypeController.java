package user.buy.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import common.util.CommonUtil;
import user.buy.service.TypeService;
import user.buy.vo.TicketType;

@WebServlet("/buy/ticket-types")
public class TypeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TypeService service;
	/**
	 * 購票頁(bookStep.html)票種資料界接 
	 * 請求方法: GET 
	 * 回應格式: JSON 
	 * 資料型態: List<TicketType> 
	 * API: /buy/ticket-types?eventId=1
	 */

	// DL 方式注入(暫時性，後續 spring-MVC 會改)
	@Override
	public void init() throws ServletException {
		service = CommonUtil.getBean(getServletContext(), TypeService.class);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 接受前端查詢 (keyword, pageNumber, pageSize)
		String eventIdTmp = req.getParameter("eventId");
		Integer eventId = Integer.parseInt(eventIdTmp);
		// 3. 將 (keyword, pageNumber, pageSize) 交給 Service 處理，回傳查詢結果
		List<TicketType> ticketTypes = service.findTicketType(eventId);
		// 4. 轉成 json 格式，並回應 json 字串
		Gson gson = new Gson();
		String jsonData = gson.toJson(ticketTypes);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

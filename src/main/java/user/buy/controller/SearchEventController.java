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

import common.vo.Payload;
import user.buy.service.SearchService;
import user.buy.service.impl.SearchServiceImpl;
import user.buy.vo.EventInfo;

/**
 * 首頁(index.html)熱門活動區塊資料界接 
 * 搜尋頁(search.html)活動區塊資料界接 
 * 請求方法: GET 
 * 回應格式: JSON 
 * 資料型態: Payload<List<EventInfo>>
 * API: /search-event?keyword="台北"&pageNumber=1&pageSize=3
 */
@WebServlet("/search-event")
public class SearchEventController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SearchService buyServiceImpl;

	public SearchEventController() {
		buyServiceImpl = new SearchServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 接受前端查詢 (keyword, pageNumber, pageSize)
		String keyword = req.getParameter("keyword");
		Integer pageNubmer = Integer.parseInt(req.getParameter("pageNumber"));
		Integer pageSize = 6; // 固定一頁6筆
		// 3. 將 (keyword, pageNumber, pageSize) 交給 Service 處理，回傳查詢結果
		Payload<List<EventInfo>> eventPayload = buyServiceImpl.searchEventByKeyword(keyword, pageNubmer, pageSize);
		// 4. 轉成 json 格式，並回應 json 字串
		Gson gson = new Gson();
		String jsonData = gson.toJson(eventPayload);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

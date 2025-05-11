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
import user.buy.service.BuyService;
import user.buy.service.impl.BuyServiceImpl;
import user.buy.vo.EventInfo;

@WebServlet("/index-search-event")
public class SearchEventController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BuyService buyServiceImpl;

	public SearchEventController() {
		buyServiceImpl = new BuyServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 接受前端查詢 keywords (若無則給定 "" 查詢)
		String keyword = req.getParameter("keyword");
		// 3. 將 keywords 交給 Service 處理，回傳查詢結果
		Payload<List<EventInfo>> payload = buyServiceImpl.searchEventByKeyword(keyword);
		// 4. 轉成 json 格式，並回應 json 字串
		Gson gson = new Gson();
		String jsonData = gson.toJson(payload);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

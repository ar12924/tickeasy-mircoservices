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

import user.buy.service.BuyService;
import user.buy.service.impl.BuyServiceImpl;
import user.buy.vo.BuyerTicket;

@WebServlet("/index-search-ticket")
public class SearchTicketController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BuyService buyServiceImpl;

	public SearchTicketController() {
		buyServiceImpl = new BuyServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 交給 Service 處理，回傳查詢結果
		List<BuyerTicket> buyerTicketList = buyServiceImpl.searchTicket();
		// 3. 將活動陣列轉成 json 格式
		Gson gson = new Gson();
		String jsonData = gson.toJson(buyerTicketList);
		// 4. 回應 json 字串
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

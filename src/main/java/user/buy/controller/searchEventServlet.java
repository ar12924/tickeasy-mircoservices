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

import user.buy.dao.BuyDao;
import user.buy.dao.impl.BuyDaoImpl;
import user.buy.vo.EventInfo;

@WebServlet("/search-event")
public class searchEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BuyDao buyDaoImpl;

	public searchEventServlet() {
		buyDaoImpl = new BuyDaoImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");

		// 1. 接受前端查詢字串(若無則給定空字串查詢)
		String keywords = req.getParameter("keywords");
		keywords = keywords == null ? "" : keywords;
		// 2. 去資料庫找活動資料
		List<EventInfo> eventInfoLists = buyDaoImpl.selectEvent(keywords);
		// 3. 創建 Gson 物件
		Gson gson = new Gson();
		// 4. 將活動陣列轉成 json 格式
		String jsonData = gson.toJson(eventInfoLists);
		// 5. 回應 json 字串(包含多個活動)
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

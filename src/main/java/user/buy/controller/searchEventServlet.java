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
public class searchEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BuyService buyServiceImpl;

	public searchEventServlet() {
		buyServiceImpl = new BuyServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 接受前端查詢 keywords (若無則給定 "" 查詢)
		String keyword = req.getParameter("keyword");
		// 3. 將 keywords 交給 Service 處理，回傳查詢結果
		List<EventInfo> eventInfoList = buyServiceImpl.searchEventByKeyword(keyword);
		// 4. 判斷回傳 list 是否為空陣列
		Payload<List<EventInfo>> payload = new Payload<>();
		if(eventInfoList.isEmpty()) {
			payload.setSuccessful(false);
			payload.setMessage("查無資料");
		}else {
			payload.setSuccessful(true);
			payload.setMessage("取得資料");
		}
		payload.setData(eventInfoList);
		// 4. 將活動陣列轉成 json 格式
		Gson gson = new Gson();
		String jsonData = gson.toJson(payload);
		// 5. 回應 json 字串
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

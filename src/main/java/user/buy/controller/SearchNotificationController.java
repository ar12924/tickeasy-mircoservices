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
import user.buy.service.SearchService;
import user.buy.vo.MemberNotification;

/**
 * 首頁(index.html)通知中心區塊資料界接 
 * 請求方法: GET 
 * 回應格式: JSON 
 * 資料型態: List<MemberNotification>
 * API: /search-notification
 */
@WebServlet("/search-notification")
public class SearchNotificationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SearchService buyServiceImpl;

	// DL 方式注入(暫時性，後續 spring-MVC 會改)
	@Override
	public void init() throws ServletException {
		buyServiceImpl = CommonUtil.getBean(getServletContext(), SearchService.class);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 指定允許所有網域
		resp.setHeader("Access-Control-Allow-Origin", "*");
		// 2. 交給 Service 處理，回傳查詢結果
		List<MemberNotification> memberNotificationList = buyServiceImpl.searchNotification();
		// 3. 將活動陣列轉成 json 格式
		Gson gson = new Gson();
		String jsonData = gson.toJson(memberNotificationList);
		// 4. 回應 json 字串
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

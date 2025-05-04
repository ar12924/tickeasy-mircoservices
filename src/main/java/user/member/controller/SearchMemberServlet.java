package user.member.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Pipe.SourceChannel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import user.member.dao.MemberDao;
import user.member.dao.impl.MemberDaoImpl;
import user.member.vo.Member;

@WebServlet("/search-member")
public class SearchMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberDao memberDaoImpl;

	@Override
	public void init() throws ServletException {
		memberDaoImpl = new MemberDaoImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 指定允許所有網域
//		resp.setHeader("Access-Control-Allow-Origin", "*");

		// 1. 前端傳來欲查詢的會員 id
		Integer member_id = Integer.parseInt(req.getParameter("id"));
		// 2. 去資料庫找該筆會員資料
		Member member = memberDaoImpl.selectMemberById(member_id);
		// 3. 創建 Gson 物件
		Gson gson = new Gson();
		// 4. member 物件轉換成 gson 字串
		String jsonData = gson.toJson(member);
		// 5. 設定回應的 Content-Type 為 application/json
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		// 6. 取得 PrintWriter 物件，回應 jsonData 資料
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}

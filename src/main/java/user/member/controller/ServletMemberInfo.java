package user.member.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.member.dao.MemberDao;
import user.member.dao.impl.MemberDaoImpl;
import user.member.vo.Member;

@WebServlet("/memberinfo")
public class ServletMemberInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberDao memberDaoImpl;

	@Override
	public void init() throws ServletException {
		try {
			memberDaoImpl = new MemberDaoImpl();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 抓資料庫的一筆 member 資料
		Member member = memberDaoImpl.selectMemberById(5);

		// 2. 測試回應該筆資料部分欄位
		PrintWriter pw = resp.getWriter();
		pw.println("id: " + member.getMember_id());
		pw.println("username: " + member.getUser_name());
		pw.println("email: " + member.getEmail());
		pw.println("phone: " + member.getPhone());
	}
}

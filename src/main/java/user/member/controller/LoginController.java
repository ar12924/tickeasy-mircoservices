package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.vo.Member;
import static user.member.util.MemberConstants.SERVICE;


@WebServlet("/user/member/login")
public class LoginController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		 try {
	            String username = req.getParameter("userName");
	            String password = req.getParameter("password");

	            if (username == null || password == null) {
	                req.setAttribute("message", "請輸入帳號與密碼");
	                req.getRequestDispatcher("login.jsp").forward(req, resp);
	                return;
	            }

	            Member member = new Member();
	            member.setUserName(username);
	            member.setPassword(password);
	            member = SERVICE.login(member);

	            if (member.isSuccessful()) {
	                if (req.getSession(false) != null) {
	                    req.changeSessionId();
	                }
	                HttpSession session = req.getSession();
	                session.setAttribute("loggedin", true);
	                session.setAttribute("member", member);
	                resp.sendRedirect("edit.jsp");
	            } else {
	                req.setAttribute("message", member.getMessage());
	                req.getRequestDispatcher("login.jsp").forward(req, resp);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            req.setAttribute("message", "系統錯誤，請稍後再試");
	            req.getRequestDispatcher("login.jsp").forward(req, resp);
	        }
	    }
	

		
//        Member member = json2Pojo(req, Member.class);
//        if (member == null) {
//            member = new Member();
//            member.setMessage("無會員資訊");
//            member.setSuccessful(false);
//            writePojo2Json(resp, member);
//            return;
//        }
//
//        member = service.login(member);
//        if (member.isSuccessful()) {
//            if (req.getSession(false) != null) {
//                req.changeSessionId();
//            }
//            HttpSession session = req.getSession();
//            session.setAttribute("loggedin", true);
//            session.setAttribute("member", member);
//        }
//        writePojo2Json(resp, member);

}



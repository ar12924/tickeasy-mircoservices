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

@WebServlet("user/member/edit")
public class EditController extends HttpServlet{
	private static final long serialVersionUID = 1L;

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String birthDate = req.getParameter("birthDate");
        String gender = req.getParameter("gender");
        String unicode = req.getParameter("unicode");

        Member member = new Member();
        member.setUserName(userName);
        member.setPassword(password);
        member.setEmail(email);
        member.setPhone(phone);
        member.setGender(gender);
        member.setUnicode(unicode);
        
        if (birthDate != null && !birthDate.isEmpty()) {
            member.setBirthDate(java.sql.Date.valueOf(birthDate));
        }

        Member result = SERVICE.editMember(member);
        req.setAttribute("message", result.getMessage());

        if (result.isSuccessful()) {
            HttpSession session = req.getSession();
            session.setAttribute("member", result);
            resp.sendRedirect("login.jsp");
        } else {
            req.getRequestDispatcher("/edit.jsp").forward(req, resp);
        }
    }
	
	}

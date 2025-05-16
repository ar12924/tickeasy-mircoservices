package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.entity.Member;

import static user.member.util.CommonUtil.json2Pojo;
import static user.member.util.CommonUtil.writePojo2Json;
import static user.member.util.MemberConstants.SERVICE;

@WebServlet("/user/member/edit")
public class EditController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final HttpSession session = req.getSession();
		final String username = ((Member) session.getAttribute("member")).getUserName();
		Member member = json2Pojo(req, Member.class);
		member.setUserName(username);
		 
		writePojo2Json(resp, SERVICE.editMember(member));
		}
	}


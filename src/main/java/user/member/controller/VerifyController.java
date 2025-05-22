package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static user.member.util.MemberConstants.SERVICE;
import static user.member.util.CommonUtil.*;

@WebServlet("/user/member/verify")
public class VerifyController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenStr = req.getParameter("token");
		boolean result = SERVICE.activateMemberByToken(tokenStr);

		if (result) {
			writeSuccess(resp, "驗證成功", null);
		} else {
			writeError(resp, "驗證失敗，請確認連結是否有效");
		}
	}

}

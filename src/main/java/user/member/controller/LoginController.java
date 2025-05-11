package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.util.CommonUtil;
import user.member.vo.Member;
import static user.member.util.MemberConstants.SERVICE;

@WebServlet("/user/member/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Member member = CommonUtil.json2Pojo(req, Member.class);
        if (member == null) {
            member = new Member();
            member.setMessage("無會員資訊");
            member.setSuccessful(false);
            CommonUtil.writePojo2Json(resp, member);
            return;
        }

        member = SERVICE.login(member);
        if (member.isSuccessful()) {
            if (req.getSession(false) != null) {
                req.changeSessionId();
            }
            HttpSession session = req.getSession();
            session.setAttribute("loggedin", true);
            session.setAttribute("member", member);
        }
        CommonUtil.writePojo2Json(resp, member);
	}
	
}

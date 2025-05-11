package user.member.controller;

import java.io.IOException;
import user.member.vo.Member;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static user.member.util.CommonUtil.writePojo2Json;


@WebServlet("/user/member/find")
public class FindController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		HttpSession session = req.getSession(false);
        Member member = session != null ? (Member) session.getAttribute("member") : null;

        if (member == null) {
            member = new Member();
            member.setSuccessful(false);
            member.setMessage("尚未登入");
        } else {
            member.setPassword(null); // 移除敏感資訊
            member.setSuccessful(true);
            member.setMessage("登入中");
        }

        writePojo2Json(resp, member);
        
    }
	
}

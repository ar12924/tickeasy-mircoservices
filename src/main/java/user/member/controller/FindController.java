package user.member.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.vo.Member;

import static user.member.util.CommonUtil.*;

@WebServlet("/user/member/find")
public class FindController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("member") == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeError(resp, "請先登入");
			return;
		}

		Member member = (Member) session.getAttribute("member");

		Map<String, Object> result = new HashMap<>();
		result.put("successful", true);
		result.put("message", "");
		result.put("userName", member.getUserName());
		result.put("nickName", member.getNickName());
		result.put("email", member.getEmail());
		result.put("unicode", member.getUnicode());

		byte[] photoBytes = member.getPhoto();
		if (photoBytes != null && photoBytes.length > 0) {
			String b64 = Base64.getEncoder().encodeToString(photoBytes);
			result.put("photo", b64);
		}

		writeSuccess(resp, "載入成功", result);
	}

}

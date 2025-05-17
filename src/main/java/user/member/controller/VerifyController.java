package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.member.entity.Member;
import user.member.service.MemberService;
import user.member.service.impl.MemberServiceImpl;
import static user.member.util.CommonUtil.writePojo2Json;

@WebServlet("/user/member/verify")
public class VerifyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private final MemberService service = new MemberServiceImpl();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tokenStr = req.getParameter("token");
        Member result = new Member();
        
        System.out.println("收到驗證請求 token: " + tokenStr); // ✅ 測試印出 token

        
        if (tokenStr == null || tokenStr.trim().isEmpty()) 
        {
        	result.setSuccessful(false);
            result.setMessage("未提供驗證 token");
        } else {
        	boolean success = service.activateMemberByToken(tokenStr);
            result.setSuccessful(success);
            result.setMessage(success ? "帳號啟用成功！" : "驗證失敗：token 無效或已過期。");
		}
        
        writePojo2Json(resp, result);
        
	}

}

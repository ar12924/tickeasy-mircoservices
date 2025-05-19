package user.member.controller;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.member.vo.Member;
import user.member.service.MemberService;
import user.member.service.impl.MemberServiceImpl;

import static user.member.util.CommonUtil.json2Pojo;
import static user.member.util.CommonUtil.writePojo2Json;

@WebServlet("/user/member/register")
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final MemberService service = new MemberServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Member input = json2Pojo(req, Member.class);
        Member result = service.register(input);     
        writePojo2Json(resp, result);                
    }
}

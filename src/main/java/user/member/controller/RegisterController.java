package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import common.util.CommonUtil;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.vo.Member;

import static user.member.util.CommonUtil.*;
import static user.member.util.CommonUtil.json2Pojo;

@WebServlet("/user/member/register")
@MultipartConfig(
		  fileSizeThreshold = 1024*1024,       // 1MB
		  maxFileSize       = 5*1024*1024,     // 5MB
		  maxRequestSize    = 10*1024*1024     // 10MB
		)
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService service;
	private MailService mailService;
	
	@Override
	public void init() throws ServletException {
		service = CommonUtil.getBean(getServletContext(), MemberService.class);
		mailService = CommonUtil.getBean(getServletContext(), MailService.class);
	}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.getRequestDispatcher("/user/member/register.html")
           .forward(req, resp);
    }
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	Member input = json2Pojo(req, Member.class);
    	
    	//  處理 photo part，轉成 byte[] 放到 entity 裡
    	Part photoPart = req.getPart("photo");
        if (photoPart != null && photoPart.getSize() > 0) {
        	input.setPhoto(photoPart.getInputStream().readAllBytes());
        }
    	
        Member result = service.register(input);  
        if (result.isSuccessful()) {
			writeSuccess(resp, "註冊成功", result);
		} else {
			writeError(resp, result.getMessage());
		}
    }
}

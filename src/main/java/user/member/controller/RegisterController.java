package user.member.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.member.service.MemberService;
import user.member.service.impl.MemberServiceImpl;
import user.member.vo.Member;

@WebServlet("/user/member/register")
public class RegisterController extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private MemberService service = new MemberServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String userName = req.getParameter("userName");
            String password = req.getParameter("password");
            String rePassword = req.getParameter("rePassword");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String birthDate = req.getParameter("birthDate");
            String gender = req.getParameter("gender");
            String idCard = req.getParameter("idCard");
            String unicode = req.getParameter("unicode");

            if (userName == null || password == null || rePassword == null || !password.equals(rePassword)) {
                req.setAttribute("message", "密碼與確認密碼不一致");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            Member member = new Member();
            member.setUserName(userName);
            member.setPassword(password);
            member.setEmail(email);
            member.setPhone(phone);
            member.setGender(gender);
            member.setIdCard(idCard);
            member.setUnicode(unicode);
            
            if (birthDate != null && !birthDate.isEmpty()) {
                member.setBirthDate(java.sql.Date.valueOf(birthDate));
            }

            member = service.register(member);

            if (member.isSuccessful()) {
                resp.sendRedirect("login.jsp");
            } else {
                req.setAttribute("message", member.getMessage());
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("message", "註冊失敗，請稍後再試");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
    }

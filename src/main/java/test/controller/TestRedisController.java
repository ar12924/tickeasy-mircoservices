package test.controller;

import com.google.gson.Gson;
import common.util.CommonUtil;
import test.service.TestRedisService;
import test.vo.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/test/redis")
public class TestRedisController extends HttpServlet {
    private TestRedisService service;

    // DL 方式注入(暫時性，後續 spring-MVC 會改)
    @Override
    public void init() throws ServletException {
        service = CommonUtil.getBean(getServletContext(), TestRedisService.class);
    }

    // 向 Redis 查詢一筆學生資料
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 指定允許所有網域
        resp.setHeader("Access-Control-Allow-Origin", "*");
        // 2. 交給 Service 處理，回傳查詢結果
        List<Student> savedOne = service.save2Students();
        Iterable<Student> foundAllTmp = service.findAllStudents();
        // 3. 轉成 json 格式，並回應 json 字串
        Gson gson = new Gson();
        String jsonData = gson.toJson(foundAllTmp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        PrintWriter pw = resp.getWriter();
        pw.print(jsonData);
    }
}

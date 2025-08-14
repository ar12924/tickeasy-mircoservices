package microservices.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 將根路徑導向到原有首頁 JSP
    @GetMapping({"/", ""})
    public String index() {
        return "/index"; // 對應 src/main/webapp/index.jsp
    }
}



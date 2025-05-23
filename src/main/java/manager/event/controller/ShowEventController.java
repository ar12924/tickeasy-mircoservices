package manager.event.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import manager.event.service.ShowEventService;
import manager.event.service.impl.ShowEventServiceImpl;
import manager.event.vo.EventInfo;


@WebServlet("/show-event")
public class ShowEventController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ShowEventService showEventService;

	public ShowEventController() {
		showEventService = new ShowEventServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<EventInfo> lalala = showEventService.showEvent();
		//  轉成 json 格式，並回應 json 字串
		Gson gson = new Gson();
		String jsonData = gson.toJson(lalala);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonData);
	}
}
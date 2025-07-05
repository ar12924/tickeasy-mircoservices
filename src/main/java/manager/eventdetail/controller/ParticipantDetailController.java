package manager.eventdetail.controller;

import com.google.gson.Gson;
import common.util.CommonUtil;
import manager.eventdetail.service.ParticipantService;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static common.util.CommonUtilNora.*;
import common.vo.Core;

@RestController
@RequestMapping("/manager/eventdetail/participants/detail")
public class ParticipantDetailController {
    @Autowired
    private ParticipantService participantService;

    @GetMapping("/{ticketId}")
    public Core<Object> getParticipantDetail(@PathVariable Integer ticketId) {
        Core<Object> core = new Core<>();
        if (ticketId == null) {
            core.setSuccessful(false);
            core.setMessage("缺少 ticketId 參數");
            return core;
        }
        try {
            Map<String, Object> detailData = participantService.getParticipantDetail(ticketId);
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(detailData);
            return core;
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage(e.getMessage());
            return core;
        }
    }
}
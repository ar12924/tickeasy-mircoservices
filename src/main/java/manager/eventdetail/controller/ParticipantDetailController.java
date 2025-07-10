package manager.eventdetail.controller;

import common.vo.Core;
import manager.eventdetail.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
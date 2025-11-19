package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.AccessLog;
import kr.co.skb.pmsa.member.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping
    public List<AccessLog> getAllAccessLogs() {
        System.out.println("[AccessLog] Get All Call");
        return accessLogService.getAllLogs();
    }
}

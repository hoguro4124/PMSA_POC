package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.WorkLog;
import kr.co.skb.pmsa.member.service.WorkLogService;
import kr.co.skb.pmsa.member.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("work_logs")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;

    @Autowired
    private JwtUtil jwtUtil;

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("Proxy-Client-IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("WL-Proxy-Client-IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("HTTP_CLIENT_IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("HTTP_X_FORWARDED_FOR"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getRemoteAddr(); }
        if ("0:0:0:0:0:0:0:1".equals(ip)) { ip = "127.0.0.1"; }
        return ip;
    }

    private String getOperatorId(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try {
                return jwtUtil.validateToken(bearerToken.substring(7)).getSubject();
            } catch (Exception e) {}
        }
        return "ANONYMOUS";
    }

    @GetMapping
    public List<WorkLog> getAllWorkLogs(
            @RequestParam(required = false) String searchOperatorId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {

        String operatorId = getOperatorId(request);
        String ip = getClientIp(request);

        System.out.println(">>> [WorkLog] 검색 요청: ID=" + searchOperatorId + ", Type=" + actionType);

        boolean isSearch = (searchOperatorId != null && !searchOperatorId.trim().isEmpty()) ||
                (actionType != null && !"ALL".equals(actionType)) ||
                (startDate != null && !startDate.isEmpty()) ||
                (endDate != null && !endDate.isEmpty());

        if (isSearch) {
            String details = String.format("검색 조건 [작업자: %s, 유형: %s, 기간: %s~%s]",
                    (searchOperatorId == null || searchOperatorId.isEmpty()) ? "-" : searchOperatorId,
                    (actionType == null || "ALL".equals(actionType)) ? "-" : actionType,
                    (startDate == null || startDate.isEmpty()) ? "-" : startDate,
                    (endDate == null || endDate.isEmpty()) ? "-" : endDate);

            workLogService.saveWorkLog(operatorId, "ALL", "작업 기록", "WORK_LOG_SEARCH", details, ip);
        } else {
            workLogService.saveWorkLog(operatorId, "ALL", "작업 기록", "WORK_LOG_VIEW", "작업 이력 전체 조회", ip);
        }

        return workLogService.getAllLogs();
    }
}
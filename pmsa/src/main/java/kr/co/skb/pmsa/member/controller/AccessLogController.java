package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.AccessLog;
import kr.co.skb.pmsa.member.service.AccessLogService;
import kr.co.skb.pmsa.member.service.WorkLogService;
import kr.co.skb.pmsa.member.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

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
    public List<AccessLog> getAllAccessLogs(
            @RequestParam(required = false) String searchUserId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {

        String operatorId = getOperatorId(request);
        String ip = getClientIp(request);

        // [수정] 검색 조건이 진짜로 입력되었는지 체크
        boolean hasSearchCondition = (searchUserId != null && !searchUserId.trim().isEmpty()) ||
                (actionType != null && !"ALL".equals(actionType)) ||
                (startDate != null && !startDate.isEmpty()) ||
                (endDate != null && !endDate.isEmpty());

        if (hasSearchCondition) {
            // 1. 검색 조건이 있을 때 -> "검색" 로그
            String details = String.format("검색 조건 [ID: %s, 유형: %s, 기간: %s~%s]",
                    (searchUserId == null || searchUserId.isEmpty()) ? "-" : searchUserId,
                    (actionType == null || "ALL".equals(actionType)) ? "-" : actionType,
                    (startDate == null || startDate.isEmpty()) ? "-" : startDate,
                    (endDate == null || endDate.isEmpty()) ? "-" : endDate);

            workLogService.saveWorkLog(operatorId, "ALL", "접속 기록", "ACCESS_LOG_SEARCH", details, ip);
        } else {
            // 2. 검색 조건이 없을 때 -> "메뉴 접근" 로그
            workLogService.saveWorkLog(operatorId, "ALL", "접속 기록", "ACCESS_LOG_VIEW", "메뉴 접근", ip);
        }

        return accessLogService.getAllLogs();
    }
}
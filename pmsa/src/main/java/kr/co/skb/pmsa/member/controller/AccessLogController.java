package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.AccessLog;
import kr.co.skb.pmsa.member.service.AccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 접속 기록(AccessLog) 조회 API를 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/access-logs") // API 기본 경로 설정
public class AccessLogController {

    private final AccessLogService accessLogService;

    @Autowired
    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    /**
     * 모든 접속 기록을 최신 순으로 조회합니다.
     * GET /api/access-logs
     * @return 최신 접속 기록 목록 (List<AccessLog>)
     */
    @GetMapping
    public ResponseEntity<List<AccessLog>> getAllAccessLogs() {
        // 서비스 계층을 호출하여 모든 로그를 조회
        List<AccessLog> logs = accessLogService.getAllLogs();

        // HTTP 200 OK와 함께 로그 목록을 반환
        return ResponseEntity.ok(logs);
    }

    // 참고: 로그 저장 로직은 LoginController에 추가해야 합니다.
}
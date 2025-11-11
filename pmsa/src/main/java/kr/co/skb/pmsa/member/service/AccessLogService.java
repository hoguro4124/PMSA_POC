

package kr.co.skb.pmsa.member.service;


import kr.co.skb.pmsa.member.entity.AccessLog;
import kr.co.skb.pmsa.member.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 접속 기록(AccessLog) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 로그 저장 및 전체 로그 조회를 담당합니다.
 */
@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @Autowired
    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    /**
     * 새로운 접속 기록을 데이터베이스에 저장합니다.
     * @param userId 기록을 남길 사용자 ID
     * @param ipAddress 접속 시 사용한 IP 주소
     * @param actionType 발생한 작업 유형 (예: "LOGIN", "LOGOUT")
     * @return 저장된 AccessLog 엔티티
     */
    @Transactional
    public AccessLog saveLog(String userId, String ipAddress, String actionType) {
        // AccessLog 엔티티 생성자: public AccessLog(String userId, String ipAddress, String actionType)
        AccessLog log = new AccessLog(userId, ipAddress, actionType);
        return accessLogRepository.save(log);
    }

    /**
     * 모든 접속 기록을 최신 순(logTime 기준)으로 조회합니다.
     * @return AccessLog 객체의 리스트
     */
    @Transactional(readOnly = true)
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAllByOrderByLogTimeDesc();
    }
}
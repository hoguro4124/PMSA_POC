
package kr.co.skb.pmsa.member.service;

import kr.co.skb.pmsa.member.entity.AccessLog;
import kr.co.skb.pmsa.member.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import org.springframework.transaction.annotation.Propagation; // Propagation import 추가
/**
 * 접속 기록(AccessLog) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 로그 저장 및 전체 로그 조회를 담당합니다.
 */
@Service
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    // 생성자 주입 (Constructor Injection) 방식을 사용하여 Repository를 초기화합니다.
    @Autowired
    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    /**
     * 새로운 접속 기록을 데이터베이스에 저장합니다.
     * DB 저장 실패 시의 오류를 명확하게 콘솔에 출력합니다.
     *
     * @param userId     기록을 남길 사용자 ID
     * @param ipAddress  접속 시 사용한 IP 주소
     * @param actionType 발생한 작업 유형 (예: "LOGIN", "LOGOUT")
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 독립 트랜잭션으로 실행
    public void saveLog(String userId, String ipAddress, String actionType) {
        // --- 1단계: 메서드 호출 확인 ---
        System.out.println("--- [AccessLogService] 로그 저장 시도 시작 ---");
        System.out.println("로그 상세 정보 -> ID: " + userId + ", IP: " + ipAddress + ", Type: " + actionType);

        try {
            // 2단계: AccessLog 엔티티 생성
            AccessLog log = new AccessLog(userId, ipAddress, actionType);

            // 3단계: 레포지토리를 통해 데이터베이스에 저장
            accessLogRepository.save(log);

            // --- 4단계: DB 저장 성공 확인 ---
            System.out.println("--- [AccessLogService] 로그 저장 성공: DB 기록 완료 logsave: dbsave good---");

        } catch (Exception e) {
            // --- 5단계: DB 저장 실패 시 예외 처리 ---
            System.err.println("--- [AccessLogService] FATAL: 데이터베이스 저장 중 심각한 오류 발생 dberr ---");
            // 정확한 오류 원인(SQL, 제약 조건 등)을 파악하기 위해 스택 트레이스 출력
            e.printStackTrace();
        }
        System.out.println("--- [AccessLogService] 로그 저장 시도 종료 log save end---");
    } // <-- saveLog 메서드 종료

    /**
     * 모든 접속 기록을 최신 순(logTime 기준)으로 조회합니다.
     * 데이터 변경이 없는 조회 작업이므로 readOnly = true로 설정합니다.
     *
     * @return AccessLog 객체의 리스트
     */
    @Transactional(readOnly = true)
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAllByOrderByLogTimeDesc();
    }
}

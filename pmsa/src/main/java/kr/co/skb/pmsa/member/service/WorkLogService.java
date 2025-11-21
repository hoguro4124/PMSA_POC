
package kr.co.skb.pmsa.member.service;

import kr.co.skb.pmsa.member.entity.WorkLog;
import kr.co.skb.pmsa.member.repository.WorkLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkLogService {

    @Autowired
    private WorkLogRepository workLogRepository;

    // 독립된 트랜잭션으로 로그 저장 (메인 로직에 영향 안 줌)
    // [수정] menuName 파라미터 추가됨
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveWorkLog(String operatorId, String targetId, String menuName, String actionType, String details, String ipAddress) {
        try {
            // 생성자에 menuName 전달
            WorkLog log = new WorkLog(operatorId, targetId, menuName, actionType, details, ipAddress);
            workLogRepository.save(log);
            System.out.println(">>> [WorkLog] 저장 완료 (" + menuName + "): " + actionType);
        } catch (Exception e) {
            System.err.println(">>> [WorkLog] 저장 실패: " + e.getMessage());
            // 로그 저장은 실패하더라도 에러를 던지지 않음 (비즈니스 로직 보호)
        }
    }

    // 로그 조회용
    public List<WorkLog> getAllLogs() {
        return workLogRepository.findAllByOrderByLogTimeDesc();
    }
}

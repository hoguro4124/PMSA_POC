package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccessLog 엔티티를 위한 JPA Repository 인터페이스입니다.
 * 데이터베이스와의 통신(저장, 조회 등)을 담당합니다.
 */
@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    /**
     * 모든 접속 기록을 최신순(logTime 기준 내림차순)으로 조회합니다.
     * JpaRepository가 메서드 이름을 분석하여 쿼리를 자동으로 생성합니다.
     * @return 최신 접속 기록 목록
     */
    List<AccessLog> findAllByOrderByLogTimeDesc();

    // 추가적인 조회 기능이 필요하다면 여기에 정의할 수 있습니다.
    // 예: List<AccessLog> findByUserIdOrderByLogTimeDesc(String userId);
}

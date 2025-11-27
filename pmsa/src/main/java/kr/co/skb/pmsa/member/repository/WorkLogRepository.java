
package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

    List<WorkLog> findAllByOrderByLogTimeDesc();

    // ▼▼▼ [추가] 작업자(User)의 권한 레벨 목록(예: 1, 2)에 해당하는 로그만 조회 ▼▼▼
    @Query("SELECT w FROM WorkLog w " +
            "WHERE w.operatorId IN (SELECT u.userId FROM User u WHERE u.accessLevel IN :levels) " +
            "ORDER BY w.logTime DESC")
    List<WorkLog> findByOperatorLevelIn(@Param("levels") List<Integer> levels);
}
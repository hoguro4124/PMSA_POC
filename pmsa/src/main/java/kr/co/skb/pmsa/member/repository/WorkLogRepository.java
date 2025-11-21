
package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    // 최신순 조회
    List<WorkLog> findAllByOrderByLogTimeDesc();
}



package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByuserIdAndPassword(String userId, String password);
    Optional<User> findByUserId(String userId);

    // 레벨별 조회
    List<User> findByAccessLevel(int accessLevel);

    // [1] 관리자 검색 (Level 1)
    @Query("SELECT u FROM User u WHERE u.accessLevel = 1 AND " +
            "(u.name LIKE %:keyword% OR u.phone LIKE %:keyword% OR u.email LIKE %:keyword%)")
    List<User> searchAdmins(@Param("keyword") String keyword);

    // ▼▼▼ [추가] 일반 사용자 검색 (Level 3) ▼▼▼
    @Query("SELECT u FROM User u WHERE u.accessLevel = 3 AND " +
            "(u.name LIKE %:keyword% OR u.phone LIKE %:keyword% OR u.email LIKE %:keyword%)")
    List<User> searchUsers(@Param("keyword") String keyword);
}

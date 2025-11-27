
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

    // 관리자 목록(Level 1, 2) 전체 조회용
    List<User> findByAccessLevelLessThan(int accessLevel);

    // [수정] 일반 사용자 검색 (Level 3) - 아이디(userId) 검색 추가
    @Query("SELECT u FROM User u WHERE u.accessLevel = 3 AND " +
            "(u.userId LIKE CONCAT('%', :keyword, '%') OR " +
            " u.name LIKE CONCAT('%', :keyword, '%') OR " +
            " u.phone LIKE CONCAT('%', :keyword, '%') OR " +
            " u.email LIKE CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);

    // [수정] 관리자 검색 (Level 1, 2) - 아이디(userId) 검색 추가
    @Query("SELECT u FROM User u WHERE u.accessLevel < 3 AND " +
            "(u.userId LIKE CONCAT('%', :keyword, '%') OR " +
            " u.name LIKE CONCAT('%', :keyword, '%') OR " +
            " u.phone LIKE CONCAT('%', :keyword, '%') OR " +
            " u.email LIKE CONCAT('%', :keyword, '%'))")
    List<User> searchAdmins(@Param("keyword") String keyword);
}

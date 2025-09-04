// users 테이블 로직 정의

package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.AdminUser;
import kr.co.skb.pmsa.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByuserIdAndPassword(String userId, String password);
    Optional<User> findByUserId(String userId);

}

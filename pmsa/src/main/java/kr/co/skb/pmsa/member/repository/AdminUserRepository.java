package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
    Optional<AdminUser> findByAdminIdAndPassword(String adminId, String password);
}

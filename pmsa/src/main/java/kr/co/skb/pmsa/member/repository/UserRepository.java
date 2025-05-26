package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<user, String> {
}

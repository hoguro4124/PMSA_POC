package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Long> {
}

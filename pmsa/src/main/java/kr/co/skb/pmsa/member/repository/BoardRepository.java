package kr.co.skb.pmsa.member.repository;

import kr.co.skb.pmsa.member.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {

}

package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.Board;
import kr.co.skb.pmsa.member.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    // 전체 조회
    @GetMapping
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public Optional<Board> getBoard(@PathVariable int id) {
        return boardRepository.findById(id);
    }

    // 게시글 등록
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        // 서버에서 받은 데이터 확인을 위한 로그
        System.out.println("서버에서 받은 데이터: " + board);

        // createdAt은 @PrePersist에서 자동 세팅
        Board savedBoard = boardRepository.save(board);

        // 저장된 게시글 출력
        System.out.println("저장된 게시글: " + savedBoard);

        return savedBoard;
    }

    // 수정 (댓글 추가 및 수정)
    @PutMapping("/{id}")
    public Board updateBoard(@PathVariable int id, @RequestBody Board board) {
        Board target = boardRepository.findById(id).orElse(null);
        if (target == null) return null;

        // 제목, 내용, 댓글 수정
        target.setTitle(board.getTitle());
        target.setContent(board.getContent());
        target.setComment(board.getComment()); // 댓글 필드 업데이트

        return boardRepository.save(target);
    }

    // 댓글 삭제 (댓글 내용을 빈 문자열로 설정)
    @Transactional
    @PutMapping("/{id}/deleteComment")
    public Board deleteComment(@PathVariable int id) {
        Board target = boardRepository.findById(id).orElse(null);
        if (target == null) {
            throw new RuntimeException("Board not found");
        }

        target.setComment("");  // 댓글 삭제 (빈 문자열로 설정)
        return boardRepository.save(target);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable int id) {
        boardRepository.deleteById(id);
    }
}

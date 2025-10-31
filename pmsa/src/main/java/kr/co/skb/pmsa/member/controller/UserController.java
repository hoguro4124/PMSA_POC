// users 테이블 API 설정

package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.util.HashUtil;
import kr.co.skb.pmsa.member.util.JwtUtil;
import kr.co.skb.pmsa.member.entity.User;
import kr.co.skb.pmsa.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;




@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 모든 사용자 조회
    @GetMapping //조회 (HTTP GET)
    public List<User> getAllUsers() {
        System.out.println("[User] GetMapping Call");
        return userRepository.findAll();
    }
    
    // ID로 특정 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        System.out.println("[User] GetMapping id search Call : "+ id);
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    // 중복 가입 확인
    @GetMapping("/check-id/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        System.out.println("[User] GetMapping check-id Call");
        boolean exists = userRepository.findByUserId(userId).isPresent();
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }




    // 사용자 추가
    @PostMapping //생성 (HTTP POST)
    public User createUser(@RequestBody User User) {
        System.out.println("[User] PostMapping Call : "+User);
        String hashedPassword = HashUtil.sha256(User.getPassword());
        User.setPassword(hashedPassword);
        return userRepository.save(User);
    }

    // 사용자 삭제
    @DeleteMapping("/{id}") //삭제 (HTTP DELETE)
    public void deleteUser(@PathVariable int id) {
        System.out.println("[User] DeleteMapping Call : "+id);
        userRepository.deleteById(id);
    }

    // 사용자 정보 수정
    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setName(updatedUser.getName());
            user.setPhone(updatedUser.getPhone());
            user.setEmail(updatedUser.getEmail());
            user.setMaAgree(updatedUser.isMaAgree());
            user.setAccessLevel(updatedUser.getAccessLevel());
            // 비밀번호가 null이 아니고 공백이 아닌 경우만 암호화 후 저장
            String pw = updatedUser.getPassword();
            if (pw != null && !pw.trim().isEmpty()) {
                String hashedPassword = HashUtil.sha256(pw);
                user.setPassword(hashedPassword);}
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {

        String userId = loginData.get("userId");
        String password = loginData.get("password");
        System.out.println("[User] PostMapping Login Call : "+userId+password);

        String hashedPassword = HashUtil.sha256(password);

        Optional<User> user = userRepository.findByuserIdAndPassword(userId, hashedPassword);
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(userId, user.get().getAccessLevel());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
}

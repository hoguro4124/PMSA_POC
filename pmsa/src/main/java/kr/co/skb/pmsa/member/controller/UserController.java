package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.service.WorkLogService;
import io.jsonwebtoken.Claims;

import kr.co.skb.pmsa.member.util.HashUtil;
import kr.co.skb.pmsa.member.util.JwtUtil;
import kr.co.skb.pmsa.member.entity.User;
import kr.co.skb.pmsa.member.repository.UserRepository;
import kr.co.skb.pmsa.member.service.AccessLogService;

import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private WorkLogService workLogService;

    // ==================================================================
    // [IP Helper]
    // ==================================================================
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("Proxy-Client-IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("WL-Proxy-Client-IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("HTTP_CLIENT_IP"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getHeader("HTTP_X_FORWARDED_FOR"); }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getRemoteAddr(); }
        if ("0:0:0:0:0:0:0:1".equals(ip)) { ip = "127.0.0.1"; }
        return ip;
    }

    // ==================================================================
    // [Token Helper]
    // ==================================================================
    private String getOperatorId(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            try {
                Claims claims = jwtUtil.validateToken(token);
                return claims.getSubject();
            } catch (Exception e) {
                return "UNKNOWN";
            }
        }
        return "ANONYMOUS";
    }

    // ==================================================================
    // [Menu Helper]
    // ==================================================================
    private String determineMenuName(String operatorId, User targetUser) {
        if (operatorId.equals(targetUser.getUserId())) return "마이페이지";
        if (targetUser.getAccessLevel() == 1) return "관리자 목록 > 상세";
        return "고객 목록 > 상세";
    }

    // 1. 관리자 목록 조회 & 검색
    @GetMapping("/admins")
    public List<User> getAllAdmins(@RequestParam(value = "keyword", required = false) String keyword, HttpServletRequest request) {
        String operatorId = getOperatorId(request);
        String ip = getClientIp(request);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String details = "관리자 검색 (키워드: " + keyword + ")";
            workLogService.saveWorkLog(operatorId, "ALL", "관리자 목록", "ADMIN_SEARCH", details, ip);
            return userRepository.searchAdmins(keyword);
        } else {
            workLogService.saveWorkLog(operatorId, "ALL", "관리자 목록", "ADMIN_LIST_VIEW", "관리자(Level 1) 전체 조회", ip);
            return userRepository.findByAccessLevel(1);
        }
    }

    // 2. 일반 고객 목록 조회 & 검색
    @GetMapping
    public List<User> getAllUsers(@RequestParam(value = "keyword", required = false) String keyword, HttpServletRequest request) {
        String operatorId = getOperatorId(request);
        String ip = getClientIp(request);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String details = "고객 검색 (키워드: " + keyword + ")";
            workLogService.saveWorkLog(operatorId, "ALL", "고객 목록", "USER_SEARCH", details, ip);
            return userRepository.searchUsers(keyword);
        } else {
            workLogService.saveWorkLog(operatorId, "ALL", "고객 목록", "USER_LIST_VIEW", "일반 사용자 전체 조회", ip);
            return userRepository.findAll();
        }
    }

    // 3. 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String operatorId = getOperatorId(request);
            String ip = getClientIp(request);
            String menuName = determineMenuName(operatorId, user);
            workLogService.saveWorkLog(operatorId, user.getUserId(), menuName, "USER_DETAIL_VIEW", "상세 정보 조회", ip);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. 중복 확인
    @GetMapping("/check-id/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        boolean exists = userRepository.findByUserId(userId).isPresent();
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    // 5. 생성
    @PostMapping
    public User createUser(@RequestBody User user, HttpServletRequest request) {
        String hashedPassword = HashUtil.sha256(user.getPassword());
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);
        String operatorId = getOperatorId(request);
        String ip = getClientIp(request);
        String menuName = "ANONYMOUS".equals(operatorId) ? "회원가입" : "관리자 목록";
        String details = "ANONYMOUS".equals(operatorId) ? "본인 가입" : "관리자 생성";
        if("ANONYMOUS".equals(operatorId)) operatorId = savedUser.getUserId();
        workLogService.saveWorkLog(operatorId, savedUser.getUserId(), menuName, "USER_CREATE", details, ip);
        return savedUser;
    }

    // 6. 삭제
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id, HttpServletRequest request) {
        Optional<User> targetUser = userRepository.findById(id);
        if(targetUser.isPresent()) {
            User user = targetUser.get();
            String operatorId = getOperatorId(request);
            String ip = getClientIp(request);
            String menuName = determineMenuName(operatorId, user);
            String actionDetail = operatorId.equals(user.getUserId()) ? "회원 탈퇴" : "관리자 강제 삭제";
            workLogService.saveWorkLog(operatorId, user.getUserId(), menuName, "USER_DELETE", actionDetail, ip);
        }
        userRepository.deleteById(id);
    }

    // 7. 수정
    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User updatedUser, HttpServletRequest request) {
        return userRepository.findById(id).map(user -> {
            String operatorId = getOperatorId(request);
            String ip = getClientIp(request);
            String details = "정보 수정 (" + updatedUser.getName() + ")";
            String menuName = determineMenuName(operatorId, user);
            workLogService.saveWorkLog(operatorId, user.getUserId(), menuName, "USER_UPDATE", details, ip);
            user.setName(updatedUser.getName());
            user.setPhone(updatedUser.getPhone());
            user.setEmail(updatedUser.getEmail());
            user.setMaAgree(updatedUser.isMaAgree());
            user.setAccessLevel(updatedUser.getAccessLevel());
            String pw = updatedUser.getPassword();
            if (pw != null && !pw.trim().isEmpty()) {
                user.setPassword(HashUtil.sha256(pw));
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("사용자 없음"));
    }

    // 8. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String userId = loginData.get("userId");
        String password = loginData.get("password");
        String ipAddress = getClientIp(request);

        System.out.println(">>> Login Request. ID: "+ userId + ", IP: " + ipAddress);

        Optional<User> user = userRepository.findByuserIdAndPassword(userId, HashUtil.sha256(password));

        if (user.isPresent()) {
            User loggedInUser = user.get();

            String type = (loggedInUser.getAccessLevel() == 1) ? "ADMIN_LOGIN" : "LOGIN";
            try { accessLogService.saveLog(userId, ipAddress, type); } catch (Exception e) {}

            // [비밀번호 재확인 로그] 토큰이 있는 상태에서 로그인 시도 시
            String currentOperatorId = getOperatorId(request);
            if (!"ANONYMOUS".equals(currentOperatorId)) {
                workLogService.saveWorkLog(currentOperatorId, userId, "마이페이지", "PASSWORD_VERIFY", "비밀번호 재확인 성공", ipAddress);
            }

            String token = jwtUtil.generateToken(userId, loggedInUser.getAccessLevel());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInUser);
            return ResponseEntity.ok(response);
        } else {
            try { accessLogService.saveLog(userId, ipAddress, "LOGIN_FAIL"); } catch (Exception e) {}
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
}

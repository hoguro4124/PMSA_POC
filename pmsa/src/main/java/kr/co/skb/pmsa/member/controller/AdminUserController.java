package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.AdminUser;
import kr.co.skb.pmsa.member.entity.User;
import kr.co.skb.pmsa.member.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.*;

// [추가] AccessLogService import
import kr.co.skb.pmsa.member.service.AccessLogService;
// [추가] IP 주소를 얻기 위한 import
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/adminusers")
public class AdminUserController {

    @Autowired
    private AdminUserRepository adminuserRepository;

    // [추가] AccessLogService 주입
    @Autowired
    private AccessLogService accessLogService;


    // 모든 사용자 조회
    @GetMapping //조회 (HTTP GET)
    public List<AdminUser> getAdminUserById() {
        System.out.println("[AdminUser] GetMapping Call");
        return adminuserRepository.findAll();
    }

    @PostMapping
    public AdminUser createAdminUser(@RequestBody AdminUser AdminUser) {
        return adminuserRepository.save(AdminUser);
    }

    @PostMapping("/login")
    //public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
    // HttpServletRequest 추가
        public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String adminId = loginData.get("adminId");
        String password = loginData.get("password");
        Optional<AdminUser> user = adminuserRepository.findByAdminIdAndPassword(adminId, password);
        if (user.isPresent()) {
            // 1. 접속 IP 주소 획득
            String ipAddress = request.getRemoteAddr();

            // 2. [핵심] Access Log 기록 저장 (관리자 로그인은 ADMIN_LOGIN으로 구분)
            accessLogService.saveLog(adminId, ipAddress, "ADMIN_LOGIN");
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }


}
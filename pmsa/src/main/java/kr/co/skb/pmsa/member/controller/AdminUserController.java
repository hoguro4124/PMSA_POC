package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.AdminUser;
import kr.co.skb.pmsa.member.entity.User;
import kr.co.skb.pmsa.member.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/adminusers")
public class AdminUserController {

    @Autowired
    private AdminUserRepository adminuserRepository;

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String adminId = loginData.get("adminId");
        String password = loginData.get("password");
        Optional<AdminUser> user = adminuserRepository.findByAdminIdAndPassword(adminId, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }


}
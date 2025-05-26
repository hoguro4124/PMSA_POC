package kr.co.skb.pmsa.member.controller;


import kr.co.skb.pmsa.member.entity.user;
import kr.co.skb.pmsa.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 모든 사용자 조회
    @GetMapping
    public List<user> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 추가
    @PostMapping
    public user createUser(@RequestBody user user) {
        return userRepository.save(user);
    }

    // 사용자 삭제
    @DeleteMapping("/{phone}")
    public void deleteUser(@PathVariable String phone) {userRepository.deleteById(phone); }

}
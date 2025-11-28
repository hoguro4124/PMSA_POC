
package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.Ad;
import kr.co.skb.pmsa.member.repository.AdRepository;
import kr.co.skb.pmsa.member.service.WorkLogService; // [추가]
import kr.co.skb.pmsa.member.util.JwtUtil; // [추가]

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.UUID;



@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private WorkLogService workLogService; // [추가]

    @Autowired
    private JwtUtil jwtUtil; // [추가]

    // [Helper]
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { ip = request.getRemoteAddr(); }
        if ("0:0:0:0:0:0:0:1".equals(ip)) { ip = "127.0.0.1"; }
        return ip;
    }

    private String getOperatorId(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try { return jwtUtil.validateToken(bearerToken.substring(7)).getSubject(); } catch (Exception e) {}
        }
        return "ANONYMOUS";
    }

    @PostMapping
    public ResponseEntity<String> registerAd(
            @RequestParam("adTitle") String adTitle,
            @RequestParam("adStartDate") String adStartDate,
            @RequestParam("adEndDate") String adEndDate,
            @RequestParam("adAttach") MultipartFile adAttach,
            @RequestParam("userId") String userId,
            HttpServletRequest request) { // request 추가

        try {
            System.out.println(">>> [AdController] 광고 등록 요청");

            // 날짜 변환 (String -> LocalDateTime)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(adStartDate, formatter); // 시작일
            LocalDateTime endDate = LocalDateTime.parse(adEndDate, formatter);   // 종료일

            // 파일 저장 경로 설정
            String baseDir = "C:\\uploads\\ad";
            File dir = new File(baseDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) System.out.println(">>> 폴더 생성: " + baseDir);
            }

            // 업로드된 파일의 원본 파일 이름
            String originalFileName = adAttach.getOriginalFilename();
            String uuid = UUID.randomUUID().toString(); // UUID를 사용하여 고유한 파일 이름 생성
            String savedFileName = uuid + "_" + originalFileName;

            // 파일을 서버에 저장
            File serverFile = new File(baseDir + File.separator + savedFileName);
            adAttach.transferTo(serverFile);
            System.out.println(">>> [SUCCESS] 파일 저장 완료: " + serverFile.getAbsolutePath());

            // 광고 객체 생성 및 DB에 저장
            Ad ad = new Ad();
            ad.setAdTitle(adTitle);
            ad.setAdStartDate(startDate); // 시작일
            ad.setAdEndDate(endDate);     // 종료일
            ad.setAdAttach(savedFileName); // 파일 경로 저장
            ad.setUserId(userId);
            ad.setAdRegDate(LocalDateTime.now()); // 등록일은 현재 시간으로 설정

            adRepository.save(ad); // 광고 데이터를 DB에 저장

            // [로그] 광고 등록
            String operatorId = getOperatorId(request);
            String ip = getClientIp(request);
            workLogService.saveWorkLog(operatorId, "ALL", "광고 등록", "AD_CREATE", "새 광고 등록: " + adTitle, ip);

            return ResponseEntity.ok("광고 등록 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실패: " + e.getMessage());
        }
    }


    // 광고 목록 조회 API (여기에 로그 추가)
    @GetMapping
    public ResponseEntity<List<Ad>> getAds(HttpServletRequest request) {
        try {
            List<Ad> ads = adRepository.findAll();

            // [로그] 광고 목록 조회
            String operatorId = getOperatorId(request);
            String ip = getClientIp(request);
            workLogService.saveWorkLog(operatorId, "ALL", "광고 목록", "AD_LIST_VIEW", "전체 광고 목록 조회", ip);

            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 첨부파일 다운로드 (로그 남기지 않음)
    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String fileName) {
        // ... 기존 코드 유지 ...
        // 파일명에 경로가 포함되어 있을 수 있으므로 이름만 추출하는 로직 필요할 수 있음
        // 여기서는 기존 로직 유지
        String filePath = "C:\\uploads\\ad\\" + fileName; // 실제 저장 경로에 맞게 수정 필요
        if(fileName.contains("\\")) filePath = fileName; // DB에 전체 경로가 있으면 그대로 사용

        File file = new File(filePath);
        if (!file.exists()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        String mimeType = "application/octet-stream";
        try { mimeType = Files.probeContentType(file.toPath()); } catch (IOException e) {}

        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .header("Content-Type", mimeType)
                .body(resource);
    }
}

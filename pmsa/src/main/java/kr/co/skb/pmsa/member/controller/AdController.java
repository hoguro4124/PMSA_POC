package kr.co.skb.pmsa.member.controller;

import kr.co.skb.pmsa.member.entity.Ad;
import kr.co.skb.pmsa.member.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private AdRepository adRepository;

    // 광고 등록 API
    @PostMapping
    public ResponseEntity<String> registerAd(
            @RequestParam("adTitle") String adTitle,
            @RequestParam("adStartDate") String adStartDate,
            @RequestParam("adEndDate") String adEndDate,
            @RequestParam("adAttach") MultipartFile adAttach,
            @RequestParam("userId") String userId) {

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

            return ResponseEntity.ok("광고 등록 성공");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("실패: " + e.getMessage());
        }
    }

    // 광고 목록 조회 API
    @GetMapping
    public ResponseEntity<List<Ad>> getAds() {
        try {
            List<Ad> ads = adRepository.findAll(); // 모든 광고 데이터를 조회
            return ResponseEntity.ok(ads); // 조회된 광고 목록을 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 오류 발생 시 500 상태 코드와 null 데이터 반환
        }
    }

    // 첨부파일 다운로드 API
    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String fileName) {
        // 파일 경로 설정
        String filePath = "C:\\uploads\\ad\\" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 파일이 없으면 404 반환
        }

        // 파일의 MIME 타입을 자동으로 감지하여 반환하도록 설정
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mimeType == null) {
            mimeType = "application/octet-stream";  // 기본 바이너리 타입 설정
        }

        // 파일이 존재하면 파일 리소스를 반환
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .header("Content-Type", mimeType)  // MIME 타입 설정
                .body(resource); // 첨부파일 다운로드 응답
    }
}

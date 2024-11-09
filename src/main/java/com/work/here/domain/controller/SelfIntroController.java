package com.work.here.domain.controller;

import com.work.here.domain.entity.enums.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.service.SelfIntroService;
import com.work.here.domain.util.JwtService;
import com.work.here.domain.entity.User;
import com.work.here.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;




import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/self-intro")
@RequiredArgsConstructor
public class SelfIntroController {

    private final SelfIntroService selfIntroService;
    private final JwtService jwtService;
    private final UserRepository userRepository; // Inject UserRepository

    //페이징
    @GetMapping(value = "/paginated", produces = "application/json")
    public ResponseEntity<Page<SelfIntroDto>> getPaginatedSelfIntroductions(
            @PageableDefault(size = 5) Pageable pageable) {
        Page<SelfIntroDto> selfIntroductions = selfIntroService.getPaginatedSelfIntroductions(pageable);
        return ResponseEntity.ok(selfIntroductions);
    }


    //학교별 필터링&전체 코드
    @GetMapping(value = "/main/school", produces = "application/json")
    public ResponseEntity<?> getSelfIntroductionsBySchool(@RequestParam(required = false) String school) {
        try {
            List<SelfIntroDto> selfIntroductions;
            if (school == null || school.isEmpty()) {
                selfIntroductions = selfIntroService.getAllSelfIntroductions();
            } else {
                School schoolEnum = School.fromString(school);
                selfIntroductions = selfIntroService.getSelfIntroductionsBySchool(schoolEnum);
            }
            return ResponseEntity.ok(selfIntroductions);
        } catch (IllegalArgumentException e) {
            // Return a clear message when an invalid school is provided
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid school provided: " + school);
        }
    }

//    자기소개에 학생, 학교 띄우기
    @GetMapping(value = "/user-info", produces = "application/json")
    public ResponseEntity<SelfIntroDto> getUserInfo(HttpServletRequest request) {
        // JWT에서 사용자 이메일 추출
        String userEmail = extractUserEmailFromJwt(request);
        // 사용자의 이름과 학교 정보 가져오기
        SelfIntroDto userInfo = selfIntroService.getUserNameAndSchool(userEmail);
        return ResponseEntity.ok(userInfo);
    }


    //자기소개 CRUD
    @PostMapping(consumes = "multipart/form-data")
    @Secured("ROLE_STUDENT")
    public ResponseEntity<String> createSelfIntroduction(
            @RequestPart(value = "title") String title,
            @RequestPart(value = "content") String content,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            SelfIntroDto selfIntroDto = new SelfIntroDto();
            selfIntroDto.setTitle(title);
            selfIntroDto.setContent(content);

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
            selfIntroDto.setUserName(user.getName());
            selfIntroDto.setUserSchool(user.getSchool());

            selfIntroService.createSelfIntroduction(selfIntroDto, userEmail, file);
            return ResponseEntity.ok("Self Introduction created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to create self introduction: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_STUDENT", "ROLE_ADMIN"})
    public ResponseEntity<String> updateSelfIntroduction(@PathVariable Long id,
                                                         @RequestPart(value = "title") String title,
                                                         @RequestPart(value = "content") String content,
                                                         @RequestPart(value = "file", required = false) MultipartFile file,
                                                         HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            SelfIntroDto selfIntroDto = new SelfIntroDto();
            selfIntroDto.setTitle(title);
            selfIntroDto.setContent(content);

            // Set userName and userSchool
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
            selfIntroDto.setUserName(user.getName());
            selfIntroDto.setUserSchool(user.getSchool());

            selfIntroService.updateSelfIntroduction(id, selfIntroDto, userEmail, file);
            return ResponseEntity.ok("Self Introduction updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update self introduction: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }


    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_STUDENT", "ROLE_ADMIN"})
    public ResponseEntity<String> deleteSelfIntroduction(@PathVariable Long id, HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            selfIntroService.deleteSelfIntroduction(id, userEmail);
            return ResponseEntity.ok("Self Introduction deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to delete self introduction: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // jwt 토큰에서 이메일 추출
    private String extractUserEmailFromJwt(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            if (jwtService.isTokenExpired(jwtToken)) {
                throw new RuntimeException("Token is expired");
            }
            return jwtService.extractEmail(jwtToken);
        } else {
            throw new RuntimeException("Authorization header is missing or invalid");
        }
    }

//포스트 띄우기
//    @GetMapping(value = "/main",produces = "application/json")
//    public ResponseEntity<List<SelfIntroDto>> getAllSelfIntroductions() {
//        List<SelfIntroDto> selfIntroductions = selfIntroService.getAllSelfIntroductions();
//        return ResponseEntity.ok(selfIntroductions);
//    }
//
//    @GetMapping(value = "/main/{id}", produces = "application/json")
//    public ResponseEntity<SelfIntroDto> getSelfIntroductionById(@PathVariable Long id) {
//        SelfIntroDto selfIntro = selfIntroService.getSelfIntroductionById(id)
//                .orElseThrow(() -> new RuntimeException("Self Introduction not found"));
//        return ResponseEntity.ok(selfIntro);
//    }



}
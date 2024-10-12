package com.work.here.domain.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.service.SelfIntroService;
import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.util.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/self-intro")
@RequiredArgsConstructor
public class SelfIntroController {

    private final SelfIntroService selfIntroService;
    private final JwtService jwtService;

    // 모든 사용자가 글을 볼 수 있음
    @GetMapping
    public ResponseEntity<List<SelfIntroDto>> getAllSelfIntroductions() {
        List<SelfIntroDto> selfIntroductions = selfIntroService.getAllSelfIntroductions();
        return ResponseEntity.ok(selfIntroductions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SelfIntroDto> getSelfIntroductionById(@PathVariable Long id) {
        SelfIntroDto selfIntro = selfIntroService.getSelfIntroductionById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found"));
        return ResponseEntity.ok(selfIntro);
    }

    // 글 작성
    @PostMapping
    @Secured("ROLE_STUDENT")
    public ResponseEntity<String> createSelfIntroduction(
            @RequestBody SelfIntroDto selfIntroDto,
            HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            selfIntroService.createSelfIntroduction(selfIntroDto, userEmail);
            return ResponseEntity.ok("Self Introduction created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to create self introduction: " + e.getMessage());
        }
    }

    // 글 수정
    @PutMapping("/{id}")
    @Secured({"ROLE_STUDENT", "ROLE_ADMIN"})  // 학생과 관리자 모두 수정 가능
    public ResponseEntity<String> updateSelfIntroduction(@PathVariable Long id,
                                                         @RequestBody SelfIntroDto selfIntroDto,
                                                         HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            // 자기소개서 업데이트
            selfIntroService.updateSelfIntroduction(id, selfIntroDto, userEmail);
            return ResponseEntity.ok("Self Introduction updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to update self introduction: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    @Secured({"ROLE_STUDENT", "ROLE_ADMIN"})  // 학생과 관리자 모두 삭제 가능
    public ResponseEntity<String> deleteSelfIntroduction(@PathVariable Long id, HttpServletRequest request) {
        try {
            String userEmail = extractUserEmailFromJwt(request);
            selfIntroService.deleteSelfIntroduction(id, userEmail);
            return ResponseEntity.ok("Self Introduction deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to delete self introduction: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // JWT에서 사용자 이메일 추출
    private String extractUserEmailFromJwt(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7); // "Bearer " 부분 제거
            if (jwtService.isTokenExpired(jwtToken)) {
                throw new RuntimeException("Token is expired");
            }
            return jwtService.extractEmail(jwtToken); // JWT에서 사용자 이메일 추출
        } else {
            throw new RuntimeException("Authorization header is missing or invalid");
        }
    }
}

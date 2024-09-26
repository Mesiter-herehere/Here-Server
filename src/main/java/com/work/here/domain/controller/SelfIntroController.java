package com.work.here.domain.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.service.SelfIntroService;
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

    @GetMapping
    @Secured("ROLE_USER") // 인증된 사용자만 접근 가능
    public ResponseEntity<List<SelfIntroDto>> getAllSelfIntroductions() {
        List<SelfIntroDto> selfIntroductions = selfIntroService.getAllSelfIntroductions();
        return ResponseEntity.ok(selfIntroductions);
    }

    @PostMapping
    @Secured("ROLE_USER") // 인증된 사용자만 접근 가능
    public ResponseEntity<String> createSelfIntroduction(
            @RequestBody SelfIntroDto selfIntroDto,
            HttpServletRequest request) {
        try {
            String userEmail = extractEmailFromJwt(request);
            selfIntroService.createSelfIntroduction(selfIntroDto, userEmail);
            return ResponseEntity.ok("Self Introduction created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to create self introduction: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Secured("ROLE_USER") // 인증된 사용자만 접근 가능
    public ResponseEntity<String> updateSelfIntroduction(@PathVariable Long id,
                                                         @RequestBody SelfIntroDto selfIntroDto) {
        try {
            selfIntroService.updateSelfIntroduction(id, selfIntroDto);
            return ResponseEntity.ok("Self Introduction updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update self introduction: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_USER") // 인증된 사용자만 접근 가능
    public ResponseEntity<String> deleteSelfIntroduction(@PathVariable Long id) {
        try {
            selfIntroService.deleteSelfIntroduction(id);
            return ResponseEntity.ok("Self Introduction deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to delete self introduction: " + e.getMessage());
        }
    }

    private String extractEmailFromJwt(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7); // "Bearer " 부분 제거
            if (jwtService.isTokenExpired(jwtToken)) {
                throw new RuntimeException("Token is expired");
            }
            return jwtService.extractUsername(jwtToken);
        } else {
            throw new RuntimeException("Authorization header is missing or invalid");
        }
    }
}

package com.work.here.domain.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.service.SelfIntroService;
import com.work.here.domain.util.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/self-intro")
@RequiredArgsConstructor
public class SelfIntroController {

    private final SelfIntroService selfIntroService;
    private final JwtService jwtService; // JwtService 인스턴스 추가

    @GetMapping
    public ResponseEntity<List<SelfIntroDto>> getAllSelfIntroductions() {
        List<SelfIntroDto> selfIntroductions = selfIntroService.getAllSelfIntroductions();
        return ResponseEntity.ok(selfIntroductions);
    }

    @PostMapping
    public ResponseEntity<String> createSelfIntroduction(
            @RequestBody SelfIntroDto selfIntroDto,
            HttpServletRequest request) {
        String userEmail = extractEmailFromJwt(request); // JWT에서 이메일 추출
        selfIntroService.createSelfIntroduction(selfIntroDto, userEmail);
        return ResponseEntity.ok("Self Introduction created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSelfIntroduction(@PathVariable Long id,
                                                         @RequestBody SelfIntroDto selfIntroDto) {
        selfIntroService.updateSelfIntroduction(id, selfIntroDto);
        return ResponseEntity.ok("Self Introduction updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSelfIntroduction(@PathVariable Long id) {
        selfIntroService.deleteSelfIntroduction(id);
        return ResponseEntity.ok("Self Introduction deleted successfully");
    }

    // JWT에서 이메일을 추출하는 메서드
    private String extractEmailFromJwt(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization").substring(7); // "Bearer " 부분 제거
        return jwtService.extractUsername(jwtToken); // JwtService 인스턴스를 통해 호출
    }
}

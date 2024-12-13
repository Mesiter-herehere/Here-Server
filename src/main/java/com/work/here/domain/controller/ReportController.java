// src/main/java/com/work/here/domain/controller/ReportController.java
package com.work.here.domain.controller;

import com.work.here.domain.service.SelfIntroService;
import com.work.here.domain.dto.ReportedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.annotation.Secured;
import com.work.here.domain.util.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ReportController {

    private final SelfIntroService selfIntroService;
    private final JwtService jwtService;

//    // 게시글 신고 API
//    @PostMapping
//    @Secured("ROLE_ADMIN")
//    public ResponseEntity<String> reportSelfIntroduction(@RequestParam Long selfIntroId,
//                                                         @RequestParam String reason,
//                                                         HttpServletRequest request) {
//        String userEmail = extractUserEmailFromJwt(request);
//        selfIntroService.reportSelfIntroduction(selfIntroId, userEmail, reason);
//        return ResponseEntity.ok("Report submitted successfully");
//    }

    // 신고된 게시글 조회 API
    @GetMapping("/reported-posts")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<ReportedDto>> getReportedSelfIntroductions() {
        List<ReportedDto> reportedPosts = selfIntroService.getReportedSelfIntroductions();
        return ResponseEntity.ok(reportedPosts);
    }

    // 게시글 삭제 API
    @DeleteMapping("/delete-post/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> deleteSelfIntroduction(@PathVariable Long id) {
        selfIntroService.deleteSelfIntroduction(id);
        return ResponseEntity.ok("Self Introduction deleted successfully");
    }

    // 유저 비활성화 API
    @PostMapping("/disable-user/{userId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> disableUser(@PathVariable Long userId) {
        selfIntroService.disableUser(userId);
        return ResponseEntity.ok("User disabled successfully");
    }

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
}
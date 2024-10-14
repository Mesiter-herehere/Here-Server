package com.work.here.domain.controller;

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

@RestController
@RequestMapping("/api/self-intro")
@RequiredArgsConstructor
public class SelfIntroController {

    private final SelfIntroService selfIntroService;
    private final JwtService jwtService;
    private final UserRepository userRepository; // Inject UserRepository

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

            // Set userName and userSchool
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
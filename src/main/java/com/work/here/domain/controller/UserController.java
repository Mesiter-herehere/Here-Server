package com.work.here.domain.controller;

import com.work.here.domain.service.UserService;
import com.work.here.domain.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        String token = userService.login(userDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok("Token is valid for user: " + userDetails.getUsername());
        } else {
            return ResponseEntity.status(403).body("Token is invalid");
        }
    }
}

package com.work.here.domain.service;

import com.work.here.domain.dto.UserDto;
import com.work.here.domain.entity.User;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.entity.enums.School;
import com.work.here.domain.repository.UserRepository;
import com.work.here.domain.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    // 회원가입 처리
    public void register(UserDto userDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // 새로운 사용자 생성
        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .school(School.fromString(userDto.getSchool())) // school 값 설정 추가
                .role(Role.ROLE_STUDENT) // 기본 역할 설정
                .build();

        // 사용자 저장
        userRepository.save(user);
    }

    // 로그인 처리
    public String login(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword())
        );

        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // UserDetails 대신 User를 직접 전달합니다.
        return jwtService.generateToken(user.getId(), user); // User의 ID와 User 객체를 전달
    }
}

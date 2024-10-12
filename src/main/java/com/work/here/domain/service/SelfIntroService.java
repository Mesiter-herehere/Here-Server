package com.work.here.domain.service;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.User;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.repository.SelfIntroRepository;
import com.work.here.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelfIntroService {

    private final SelfIntroRepository selfIntroRepository;
    private final UserRepository userRepository;

    // 전체 자기소개서 리스트 가져오기
    public List<SelfIntroDto> getAllSelfIntroductions() {
        return selfIntroRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 특정 ID의 자기소개서를 가져오는 메서드
    public Optional<SelfIntroDto> getSelfIntroductionById(Long id) {
        return selfIntroRepository.findById(id)
                .map(this::mapToDto);
    }

    // 자기소개서 생성
    public void createSelfIntroduction(SelfIntroDto selfIntroDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail) // 이메일로 사용자 찾기
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        SelfIntro selfIntroduction = new SelfIntro();
        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());
        selfIntroduction.setImageUrl(selfIntroDto.getImageUrl());
        selfIntroduction.setUser(user);

        selfIntroRepository.save(selfIntroduction);
    }

    // 자기소개서 업데이트
    public void updateSelfIntroduction(Long id, SelfIntroDto selfIntroDto, String userEmail) {
        SelfIntro selfIntroduction = selfIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with ID: " + id));

        // 권한 체크
        if (!isOwnerOrAdmin(selfIntroduction, userEmail)) {
            throw new RuntimeException("You are not authorized to update this self introduction");
        }

        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());
        selfIntroduction.setImageUrl(selfIntroDto.getImageUrl());

        selfIntroRepository.save(selfIntroduction);
    }

    // 자기소개서 삭제
    public void deleteSelfIntroduction(Long id, String userEmail) {
        SelfIntro selfIntroduction = selfIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with ID: " + id));

        // 권한 체크
        if (!isOwnerOrAdmin(selfIntroduction, userEmail)) {
            throw new RuntimeException("You are not authorized to delete this self introduction");
        }

        selfIntroRepository.delete(selfIntroduction);
    }

    // 글 작성자 또는 관리자인지 확인
    public boolean isOwnerOrAdmin(SelfIntro selfIntroduction, String userEmail) {
        Long introUserId = selfIntroduction.getUser().getId(); // 작성자의 사용자 ID 가져오기
        User user = userRepository.findByEmail(userEmail) // 이메일로 사용자 찾기
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // 작성자이거나 관리자인지 확인
        return introUserId.equals(user.getId()) || user.getRole() == Role.ROLE_ADMIN;
    }

    // Entity -> DTO 변환
    private SelfIntroDto mapToDto(SelfIntro selfIntro) {
        SelfIntroDto dto = new SelfIntroDto();
        dto.setTitle(selfIntro.getTitle());
        dto.setContent(selfIntro.getContent());
        dto.setImageUrl(selfIntro.getImageUrl());

        // User에서 이름과 학교 정보 받아오기
        User user = selfIntro.getUser();
        dto.setUserName(user.getName());  // User 테이블에서 이름 가져오기
        dto.setUserSchool(user.getSchool());  // User 테이블에서 학교 가져오기

        return dto;
    }
}

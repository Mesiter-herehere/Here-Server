package com.work.here.domain.service;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.User;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.repository.SelfIntroRepository;
import com.work.here.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 자기소개서 생성
    public void createSelfIntroduction(SelfIntroDto selfIntroDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        SelfIntro selfIntroduction = new SelfIntro();
        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());
        selfIntroduction.setImageUrl(selfIntroDto.getImageUrl());
        selfIntroduction.setUser(user);

        selfIntroRepository.save(selfIntroduction);
    }

    // 자기소개서 업데이트
    public void updateSelfIntroduction(Long id, SelfIntroDto selfIntroDto) {
        SelfIntro selfIntroduction = selfIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with id: " + id));

        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());
        selfIntroduction.setImageUrl(selfIntroDto.getImageUrl());

        selfIntroRepository.save(selfIntroduction);
    }

    // 자기소개서 삭제
    public void deleteSelfIntroduction(Long id) {
        SelfIntro selfIntroduction = selfIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with id: " + id));
        selfIntroRepository.delete(selfIntroduction);
    }

    // Entity -> DTO 변환
    private SelfIntroDto mapToDto(SelfIntro selfIntro) {
        SelfIntroDto dto = new SelfIntroDto();
        dto.setTitle(selfIntro.getTitle());
        dto.setContent(selfIntro.getContent());
        dto.setImageUrl(selfIntro.getImageUrl());
        return dto;
    }
}

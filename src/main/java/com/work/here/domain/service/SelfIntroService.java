package com.work.here.domain.service;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.User;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.entity.enums.School;
import com.work.here.domain.repository.SelfIntroRepository;
import com.work.here.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelfIntroService {

    private final SelfIntroRepository selfIntroRepository;
    private final UserRepository userRepository;
    private final String[] allowedFileExtensions = {".jpg", ".jpeg", ".png", ".gif"};


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
    public void createSelfIntroduction(SelfIntroDto selfIntroDto, String userEmail, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(userEmail) // 이메일로 사용자 찾기
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        String imageUrl = saveFile(file); // 파일 처리 로직 추가

        SelfIntro selfIntroduction = new SelfIntro();
        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());
        selfIntroduction.setImageUrl(imageUrl); // 저장된 이미지 URL 설정
        selfIntroduction.setUser(user);

        selfIntroRepository.save(selfIntroduction);
    }

    // 자기소개서 업데이트
    public void updateSelfIntroduction(Long id, SelfIntroDto selfIntroDto, String userEmail, MultipartFile file) throws IOException {
        SelfIntro selfIntroduction = selfIntroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with ID: " + id));

        // 권한 체크
        if (!isOwnerOrAdmin(selfIntroduction, userEmail)) {
            throw new RuntimeException("You are not authorized to update this self introduction");
        }

        // 파일 처리 로직 추가 (파일이 존재하는 경우에만)
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file);
            selfIntroduction.setImageUrl(imageUrl); // 저장된 이미지 URL 설정
        }

        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());

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

    // 파일을 저장하고 URL을 반환하는 메서드
    public String saveFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 파일 확장자 검사
            String originalFileName = file.getOriginalFilename();
            if (!isAllowedFileExtension(originalFileName)) {
                throw new IOException("허용되지 않은 파일 형식입니다.");
            }

            // 파일 이름 중복 방지를 위해 시간 정보를 추가
            String fileName = System.currentTimeMillis() + "_" + originalFileName;
            String uploadDirPath = "C:/uploads/"; // 파일을 저장할 디렉토리 경로 (절대 경로로 변경 가능)
            String filePath = Paths.get(uploadDirPath, fileName).toString(); // 파일을 저장할 경로 설정

            // 파일을 저장할 디렉토리 존재 확인 및 생성
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 디렉토리 생성
            }

            // 파일 저장
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);

            return filePath; // 저장된 파일 경로 반환
        }
        return null; // 파일이 없을 경우 null 반환
    }

    // 허용된 파일 확장자인지 확인하는 함수
    private boolean isAllowedFileExtension(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        for (String ext : allowedFileExtensions) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
//    학교별 필터링 메소드

    public List<SelfIntroDto> getSelfIntroductionsBySchool(School school) {
        return selfIntroRepository.findAll()
                .stream()
                .filter(selfIntro -> selfIntro.getUser().getSchool().equals(school))  // equals로 학교 비교
                .map(this::mapToDto)
                .collect(Collectors.toList());
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


    public Page<SelfIntroDto> getPaginatedSelfIntroductions(Pageable pageable) {
        return selfIntroRepository.findAll(pageable)
                .map(this::mapToDto);
    }

//    //자기소개 글이 존재하는지 확인
//    public Optional<SelfIntroDto> getSelfIntroductionByUser(String userEmail) {
//        return selfIntroRepository.findByUserEmail(userEmail)
//                .map(this::mapToDto);
//    }
}
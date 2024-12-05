package com.work.here.domain.service;

import com.work.here.domain.entity.Report;
import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.User;
import com.work.here.domain.dto.SelfIntroDto;
import com.work.here.domain.entity.enums.Role;
import com.work.here.domain.entity.enums.School;
import com.work.here.domain.repository.ReportRepository;
import com.work.here.domain.repository.SelfIntroRepository;
import com.work.here.domain.repository.UserRepository;
import com.work.here.domain.entity.enums.ContentActivity;
import com.work.here.domain.dto.ReportedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelfIntroService {



    private final ReportRepository reportRepository;
    private final SelfIntroRepository selfIntroRepository;
    private final UserRepository userRepository;
    private final String[] allowedFileExtensions = {".jpg", ".jpeg", ".png", ".svg"};

    //  페이징
    public Page<SelfIntroDto> getPaginatedSelfIntroductions(Pageable pageable) {
        return selfIntroRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    // school 필터링된 페이징 조회
    public Page<SelfIntroDto> getPaginatedSelfIntroductionsBySchool(School school, Pageable pageable) {
        return selfIntroRepository.findByUserSchool(school, pageable)
                .map(this::mapToDto);
    }

    // 자기소개서 상세 조회
    public SelfIntroDto getSelfIntroductionByUserEmail(String userEmail) {
        SelfIntro selfIntro = selfIntroRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found for user with email: " + userEmail));

        // SelfIntro 엔티티를 DTO로 변환
        return mapToDto(selfIntro);
    }




    // 자기소개서 생성
    public void createSelfIntroduction(SelfIntroDto selfIntroDto, String userEmail, MultipartFile file) throws IOException {
        if (userHasSelfIntro(userEmail)) {
            throw new RuntimeException("User already has a self introduction");
        }

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
    public void updateSelfIntroductionByEmail(SelfIntroDto selfIntroDto, String userEmail, MultipartFile file) throws IOException {
        // 사용자의 자기소개 검색 (이메일 기반)
        SelfIntro selfIntroduction = selfIntroRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found for user with email: " + userEmail));

        // 파일 처리 로직 추가 (파일이 존재하는 경우에만)
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveFile(file); // 파일 저장
            selfIntroduction.setImageUrl(imageUrl); // 저장된 이미지 URL 설정
        }

        // 제목과 내용 업데이트
        selfIntroduction.setTitle(selfIntroDto.getTitle());
        selfIntroduction.setContent(selfIntroDto.getContent());

        // 업데이트된 객체 저장
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

        if (selfIntroduction.getImageUrl() != null) {
            deleteFile(selfIntroduction.getImageUrl());
        }

        selfIntroRepository.delete(selfIntroduction);
    }

    // 파일 삭제 메서드 추가
    public void deleteFile(String filePath) {
        Path path = Paths.get("C:/uploads/", Paths.get(filePath).getFileName().toString());
        File file = path.toFile();
        if (file.exists()) {
            file.delete();
        }
    }

    // 글 작성자 또는 관리자인지 확인
    public boolean isOwnerOrAdmin(SelfIntro selfIntroduction, String userEmail) {
        Long introUserId = selfIntroduction.getUser().getId(); // 작성자의 사용자 ID 가져오기
        User user = userRepository.findByEmail(userEmail) // 이메일로 사용자 찾기
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // 작성자이거나 관리자인지 확인
        return introUserId.equals(user.getId()) || user.getRole() == Role.ROLE_ADMIN;
    }

    // 파일을 저장
    public String saveFile(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 파일 확장자 검사
            String originalFileName = file.getOriginalFilename();
            if (!isAllowedFileExtension(originalFileName)) {
                throw new IOException("허용되지 않은 파일 형식입니다.");
            }

            // 파일 이름 중복 방지를 위해 시간 정보를 추가
            String fileName = System.currentTimeMillis() + "_" + originalFileName;
            Path uploadDirPath = Paths.get("C:/uploads/"); // 파일을 저장할 디렉토리 경로 (절대 경로로 변경 가능)

            // 파일을 저장할 디렉토리 존재 확인 및 생성
            if (!uploadDirPath.toFile().exists()) {
                uploadDirPath.toFile().mkdirs(); // 디렉토리 생성
            }

            // 파일 저장
            Path destinationFilePath = uploadDirPath.resolve(fileName);
            file.transferTo(destinationFilePath.toFile());

            return fileName; // 파일 이름만 반환
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


    // Entity -> DTO 변환
    private SelfIntroDto mapToDto(SelfIntro selfIntro) {
        SelfIntroDto dto = new SelfIntroDto();
        dto.setTitle(selfIntro.getTitle());
        dto.setContent(selfIntro.getContent());
        dto.setImageUrl(convertToHttpUrl(selfIntro.getImageUrl()));

        // User에서 이름과 학교 정보 받아오기
        User user = selfIntro.getUser();
        dto.setUserName(user.getName());  // User 테이블에서 이름 가져오기
        dto.setUserSchool(user.getSchool());  // User 테이블에서 학교 가져오기

        return dto;
    }

    // 파일 경로를 URL로 변환
    private String convertToHttpUrl(String filePath) {
        if (filePath != null) {
            String fileName = Paths.get(filePath).getFileName().toString();
            return "https://endlessly-cuddly-salmon.ngrok-free.app/uploads/" + fileName;
        }
        return null;
    }

    //학교 정보와 이름 가져오기
    public SelfIntroDto getUserNameAndSchool(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        SelfIntroDto selfIntroDto = new SelfIntroDto();
        selfIntroDto.setUserName(user.getName());
        selfIntroDto.setUserSchool(user.getSchool());

        return selfIntroDto;
    }

    //신고 기능
    public void reportSelfIntroduction(Long selfIntroId, String userEmail, String reason) {
        SelfIntro selfIntro = selfIntroRepository.findById(selfIntroId)
                .orElseThrow(() -> new RuntimeException("Self Introduction not found with ID: " + selfIntroId));

        User reporter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Report report = new Report(selfIntro, reporter, reason);
        reportRepository.save(report);

        selfIntro.incrementReportCount();
        if (selfIntro.getReportCount() >= 5) {
            selfIntro.changeActivity(ContentActivity.FLAGGED);
        }
        selfIntroRepository.save(selfIntro);
    }

    // 신고된 게시글 조회(5회 이상 신고된 게시글)
    public List<ReportedDto> getReportedSelfIntroductions() {
        List<SelfIntro> reportedSelfIntros = selfIntroRepository.findByReportCountGreaterThanEqual(5);
        return reportedSelfIntros.stream()
                .flatMap(selfIntro -> selfIntro.getReports().stream()
                        .map(report -> new ReportedDto(
                                selfIntro.getId(),
                                selfIntro.getTitle(),
                                selfIntro.getUser().getEmail(),
                                selfIntro.getUser().getName(),
                                report.getReporter().getEmail(),
                                report.getReporter().getName(),
                                report.getReason()
                        ))
                )
                .collect(Collectors.toList());
    }

    // 게시글 삭제
    public void deleteSelfIntroduction(Long id) {
        selfIntroRepository.deleteById(id);
    }

    // 유저 비활성화
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!user.isEnabled()) {
            // 이미 비활성화된 경우 로그를 남기거나 무시
            return;
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    // 유저 자기소개서 여부 확인
    public boolean userHasSelfIntro(String userEmail) {
        return selfIntroRepository.existsByUserEmail(userEmail);
    }






//    // 학교별 필터링 메소드
//    public List<SelfIntroDto> getSelfIntroductionsBySchool(School school) {
//        return selfIntroRepository.findAll()
//                .stream()
//                .filter(selfIntro -> selfIntro.getUser().getSchool().equals(school))  // equals로 학교 비교
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }

// 전체 자기소개서 리스트 가져오기
//    public List<SelfIntroDto> getAllSelfIntroductions() {
//        return selfIntroRepository.findAll()
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }


}
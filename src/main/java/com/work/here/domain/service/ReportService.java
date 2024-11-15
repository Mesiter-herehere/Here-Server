//package com.work.here.domain.service;
//
//import com.work.here.domain.entity.SelfIntroReport;
//import com.work.here.domain.entity.User;
//import com.work.here.domain.entity.UserReport;
//import com.work.here.domain.entity.enums.UserActivity;
//import com.work.here.domain.repository.SelfIntroRepository;
//import com.work.here.domain.repository.UserReportRepository;
//import com.work.here.domain.repository.UserRepository;
//import com.work.here.domain.entity.SelfIntro;
//import com.work.here.domain.repository.SelfIntroReportRepository;
//import com.work.here.domain.entity.enums.ContentActivity;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.parameters.P;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class ReportService {
//
//    private static final int REPORT_LIMIT = 10;
//    private final UserReportRepository userReportRepository;
//    private final UserRepository userRepository;
//    private final SelfIntroRepository selfIntroRepository;
//    private final SelfIntroReportRepository selfIntroReportRepository;
//
//    @Transactional
//    public void reportUser(Long reporterId, Long reportedUserId, String text) {
//
//        validateSelfReport(reporterId, reportedUserId);
//
//        // 신고당한 유저가 존재하는지 체크
//        User reportedUser = userRepository.findById(reportedUserId)
//                .orElseThrow(() -> new IllegalArgumentException("Reported user not found"));
//
//        // 신고당한 유저가 이미 ban 상태인지 체크
//        validateUserBanned(reportedUser);
//
//        // 신고자 정보 조회
//        User reporter = getReporterEntityById(reporterId);
//
//        // 이미 신고한 유저인지 체크
//        if (userReportRepository.existsByReporterAndReportedUser(reporter, reportedUser)) {
//            return; // 이미 신고한 유저라면 신고를 접수하지 않음
//        }
//
//        // 신고 저장
//        userReportRepository.save(UserReport.of(reporter, reportedUser, text));
//
//        // 신고당한 횟수가 REPORT_LIMIT을 초과하면 활동 상태 변경
//        if (userReportRepository.countByReportedUser(reportedUser) > REPORT_LIMIT) {
//            reportedUser.changeActivity(UserActivity.FLAGGED); // 활동 상태를 FLAGGED로 변경
//        }
//    }
//
//    @Transactional
//    public void reportSelfIntro(Long reporterId, Long reportedPostId, String text) {
//
//        // 신고할 SelfIntro 엔티티 확인
//        SelfIntro reportedSelfIntro = selfIntroRepository.findById(reportedPostId)
//                .orElseThrow(() -> new IllegalArgumentException("Reported post not found"));
//
//        validateSelfReport(reporterId, reportedSelfIntro.getUser().getId());
//        validatedRestrictedContent(reportedSelfIntro);
//
//        User reporter = getReporterEntityById(reporterId);
//
//        // 이미 신고한 게시물인지 확인
//        if (selfIntroReportRepository.existsByReporterAndReportedSelfIntro(reporter, reportedSelfIntro)) {
//            return; // 이미 신고된 게시물일 경우 종료
//        }
//
//        // 신고 저장
//        selfIntroReportRepository.save(SelfIntroReport.of(reporter, reportedSelfIntro, text));
//
//        // 신고 횟수가 REPORT_LIMIT을 초과하면 활동 상태 변경
//        if (selfIntroReportRepository.countByReportedSelfIntro(reportedSelfIntro) > REPORT_LIMIT) {
//            reportedSelfIntro.changeActivity(ContentActivity.FLAGGED); // 활동 상태를 RESTRICTED로 변경
//        }
//    }
//
//    private User getReporterEntityById(Long reporterId) {
//        return userRepository.findById(reporterId)
//                .orElseThrow(() -> new IllegalArgumentException("Reporter not found"));
//    }
//
//    private void validateUserBanned(User reportedUserEntity) {
//        if (reportedUserEntity.getUserActivity() == UserActivity.BAN) {
//            throw new IllegalStateException("User is banned");
//        }
//    }
//
//    private void validateSelfReport(Long reporterId, Long reportedUId) {
//        if (reporterId.equals(reportedUId)) {
//            throw new IllegalArgumentException("You cannot report yourself");
//        }
//    }
//
//    private void validatedRestrictedContent(SelfIntro reportedPostEntity) {
//        if (reportedPostEntity.getContentActivity() == ContentActivity.RESTRICTED) {
//            throw new IllegalStateException("Content is restricted");
//        }
//    }
//
//
////    private void validatedRestrictedContent(PostEntity reportedPostEntity) {
////        if (reportedPostEntity.getContentActivity() == ContentActivity.RESTRICTED) {
////            throw new AppException(ErrorCode.RESTRICTED_CONTENT);
////        }
////    }
//}

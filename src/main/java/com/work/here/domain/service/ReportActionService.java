//package com.work.here.domain.service;
//
//import com.work.here.domain.entity.User;
//import com.work.here.domain.entity.SelfIntro;
//import com.work.here.domain.entity.UserReport;
//import com.work.here.domain.entity.SelfIntroReport;
//import com.work.here.domain.entity.enums.UserActivity;
//import com.work.here.domain.entity.enums.ContentActivity;
//import com.work.here.domain.exception.AppException;
//import com.work.here.domain.exception.ErrorCode;
//import com.work.here.domain.repository.UserReportRepository;
//import com.work.here.domain.repository.UserRepository;
//import com.work.here.domain.repository.SelfIntroReportRepository;
//import com.work.here.domain.repository.SelfIntroRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class ReportActionService {
//
//    private final UserReportRepository userReportRepository;
//    private final UserRepository userRepository;
//    private final SelfIntroReportRepository selfIntroReportRepository;
//    private final SelfIntroRepository selfIntroRepository;
//
//    // 1. 유저 활동 상태로 사용자 페이징 조회
//    @Transactional(readOnly = true)
//    public Page<User> getUserByActivity(UserActivity userActivity, Pageable pageable) {
//        return userRepository.findByUserActivity(userActivity, pageable)
//                .map(User::fromEntity);
//    }
//
//    // 2. 유저 신고 내역 페이징 조회
//    @Transactional(readOnly = true)
//    public Page<UserReport> getUserReportRecord(Long userId, Pageable pageable) {
//        User userEntity = findUserByIdOrThrow(userId);
//        return userReportRepository.findByReportedUser(userEntity, pageable)
//                .map(UserReport::fromEntity);
//    }
//
//    // 3. 게시글 활동 상태로 게시글 페이징 조회
//    @Transactional(readOnly = true)
//    public Page<Post> getPostByActivity(ContentActivity contentActivity, Pageable pageable) {
//        return postRepository.findByContentActivity(contentActivity, pageable)
//                .map(Post::fromEntity);
//    }
//
//    // 4. 게시글 신고 내역 페이징 조회
//    @Transactional(readOnly = true)
//    public Page<PostReport> getPostReportRecord(Long postId, Pageable pageable) {
//        Post postEntity = findPostByIdOrThrow(postId);
//        return postReportRepository.findByReportedPost(postEntity, pageable)
//                .map(PostReport::fromEntity);
//    }
//
//    // 5. 유저 활동 상태 변경
//    @Transactional
//    public User changeUserActivity(UserActivity userActivity, Long userId) {
//        User userEntity = findUserByIdOrThrow(userId);
//        userEntity.changeActivity(userActivity);
//        return User.fromEntity(userEntity);
//    }
//
//    // 6. 게시글 활동 상태 변경
//    @Transactional
//    public Post changePostActivity(ContentActivity contentActivity, Long postId) {
//        Post postEntity = findPostByIdOrThrow(postId);
//        postEntity.changeActivity(contentActivity);
//        return Post.fromEntity(postEntity);
//    }
//
//    // 유저 ID로 조회하는 메서드, 없으면 예외 발생
//    private User findUserByIdOrThrow(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//    }
//
//    // 게시글 ID로 조회하는 메서드, 없으면 예외 발생
//    private Post findPostByIdOrThrow(Long postId) {
//        return postRepository.findById(postId)
//                .orElseThrow(() -> new AppException(ErrorCode.POST_DOES_NOT_EXIST));
//    }
//}

//package com.work.here.domain.entity;
//
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.FilterDef;
//import org.hibernate.annotations.ParamDef;
//import org.hibernate.annotations.SQLDelete;
//import java.time.LocalDateTime;
//
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SQLDelete(sql = "UPDATE selfintro_report SET deleted_at = NOW() WHERE id=?")
//@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
//@Table(name = "\"post_report\"")
//public class SelfIntroReport {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;  // 'Id'를 'id'로 변경
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reporter_id")
//    private User reporter;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reported_Selfintro_id")
//    private SelfIntro reportedSelfIntro;
//
//    @Column(columnDefinition = "text")
//    private String content;
//
//    @Column(name = "reported_at")
//    private LocalDateTime reportedAt;
//
//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;
//
//    // 'reportedAt' 필드를 생성자에서 초기화
//    private SelfIntroReport(User reporter, SelfIntro reportedSelfIntro, String content) {
//        this.reporter = reporter;
//        this.reportedSelfIntro = reportedSelfIntro;
//        this.content = content;
//        this.reportedAt = LocalDateTime.now();  // 'reportedAt'을 생성자에서 직접 설정
//    }
//
//    public static SelfIntroReport of(User reporter, SelfIntro reportedSelfIntro, String content) {
//        return new SelfIntroReport(reporter, reportedSelfIntro, content);
//    }
//}

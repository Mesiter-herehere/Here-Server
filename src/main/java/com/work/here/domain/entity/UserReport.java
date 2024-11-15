//package com.work.here.domain.entity;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.Filter;
//import org.hibernate.annotations.FilterDef;
//import org.hibernate.annotations.ParamDef;
//import org.hibernate.annotations.SQLDelete;
//import java.time.LocalDateTime;
//
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SQLDelete(sql = "UPDATE user_report SET deleted_at = NOW() WHERE id=?")
//@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
//@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
//@Table(name = "\"user_report\"")
//
//public class UserReport {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reporter_id")
//    private User reporter;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reported_user_id")
//    private User reportedUser;
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
//    @PostPersist
//    private void setCreatedAt() {
//        reportedAt = LocalDateTime.now();
//    }
//
//    private UserReport(User reporter, User reportedUser, String content) {
//        this.reporter = reporter;
//        this.reportedUser = reportedUser;
//        this.content = content;
//    }
//
//    public static UserReport of(User reporter, User reportedUser, String content) {
//        return new UserReport(reporter, reportedUser, content);
//    }
//}

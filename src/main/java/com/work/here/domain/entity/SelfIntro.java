// src/main/java/com/work/here/domain/entity/SelfIntro.java
package com.work.here.domain.entity;

import com.work.here.domain.entity.enums.ContentActivity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public class SelfIntro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1500)
    private String content;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private ContentActivity contentActivity = ContentActivity.GENERAL;

    @Column(nullable = false)
    private int reportCount = 0;

    @OneToMany(mappedBy = "selfIntro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Getter
    private List<Report> reports;

    public void incrementReportCount() {
        this.reportCount++;
    }

    public void changeActivity(ContentActivity newActivity) {
        this.contentActivity = newActivity;
    }

}
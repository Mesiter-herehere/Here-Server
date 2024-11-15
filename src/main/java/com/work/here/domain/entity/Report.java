package com.work.here.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "self_intro_id")
    private SelfIntro selfIntro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    private String reason;

    public Report(SelfIntro selfIntro, User reporter, String reason) {
        this.selfIntro = selfIntro;
        this.reporter = reporter;
        this.reason = reason;
    }
}
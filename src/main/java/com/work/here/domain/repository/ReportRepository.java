package com.work.here.domain.repository;


import com.work.here.domain.entity.Report;
import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsBySelfIntroAndReporter(SelfIntro selfIntro, User reporter);
}
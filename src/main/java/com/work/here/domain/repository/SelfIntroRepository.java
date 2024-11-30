package com.work.here.domain.repository;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.enums.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;


public interface SelfIntroRepository extends JpaRepository<SelfIntro, Long> {
    Page<SelfIntro> findByUserSchool(School school, Pageable pageable);
    List<SelfIntro> findByReportCountGreaterThanEqual(int reportCount);
    boolean existsByUserEmail(String userEmail);
    Optional<SelfIntro> findByIdAndUserEmail(Long id, String userEmail);



}

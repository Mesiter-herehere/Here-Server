package com.work.here.domain.repository;

import com.work.here.domain.entity.SelfIntro;
import com.work.here.domain.entity.enums.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface SelfIntroRepository extends JpaRepository<SelfIntro, Long> {
    Page<SelfIntro> findByUserSchool(School school, Pageable pageable);

}

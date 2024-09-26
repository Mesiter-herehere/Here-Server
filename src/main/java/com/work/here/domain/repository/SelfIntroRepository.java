package com.work.here.domain.repository;

import com.work.here.domain.entity.SelfIntro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroRepository extends JpaRepository<SelfIntro, Long> {
}
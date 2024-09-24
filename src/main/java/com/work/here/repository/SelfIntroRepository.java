package com.work.here.repository;

import com.work.here.entity.SelfIntro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroRepository extends JpaRepository<SelfIntro, Long> {
}
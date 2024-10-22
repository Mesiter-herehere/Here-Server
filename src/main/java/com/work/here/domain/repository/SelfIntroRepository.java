package com.work.here.domain.repository;

import com.work.here.domain.entity.SelfIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SelfIntroRepository extends JpaRepository<SelfIntro, Long>, PagingAndSortingRepository<SelfIntro, Long> {
}
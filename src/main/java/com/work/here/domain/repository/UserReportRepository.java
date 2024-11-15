//package com.work.here.domain.repository;
//
//import com.work.here.domain.entity.UserReport;
//import com.work.here.domain.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserReportRepository extends JpaRepository<UserReport, Long> {
//    long countByReportedUser(User reportedUser);
//    boolean existsByReporterAndReportedUser(User reporter, User reportedUser);
//}
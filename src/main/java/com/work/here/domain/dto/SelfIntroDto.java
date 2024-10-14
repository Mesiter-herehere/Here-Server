package com.work.here.domain.dto;

import com.work.here.domain.entity.enums.School; // School 열거형 import 추가
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfIntroDto {
    private String title;
    private String content;
    private String imageUrl;
    //    private Long userId;
    private String userName;    // 작성자 이름
    private School userSchool;  // 작성자 학교 (열거형 School)
}

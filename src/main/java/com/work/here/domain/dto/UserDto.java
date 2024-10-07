package com.work.here.domain.dto;

import lombok.Data;
import com.work.here.domain.entity.User;
import com.work.here.domain.entity.enums.School;
import com.work.here.domain.entity.enums.Role;

@Data
public class UserDto {
    private String email;
    private String password;
    private String name;
    private String school;
    private String role;




    public User toEntity() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .school(School.fromString(this.school)) // Enum으로 변환
                .password(this.password)
                .role(Role.valueOf(this.role)) // Enum으로 변환
                .build();
    }
}

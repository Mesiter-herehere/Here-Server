package com.work.here.domain.dto;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String password;
    private String name;
    private String school;
}

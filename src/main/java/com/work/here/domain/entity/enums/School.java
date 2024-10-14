package com.work.here.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum School {
    GWANGJU_SOFTWARE_MAESTER("광주소프트웨어마이스터고등학교"),
    DAE_SOFTWARE_MAESTER("대소프트웨어마이스터고등학교"),
    DAEJEON_SOFTWARE_MAESTER("대전소프트웨어마이스터고등학교"),
    BUSAN_SOFTWARE_MAESTER("부산소프트웨어마이스터고등학교");

    private String schoolName;

    School(String schoolName) {
        this.schoolName = schoolName;
    }

    @JsonValue
    public String getSchoolName() {
        return schoolName;
    }

    @JsonCreator
    public static School fromString(String value) {
        for (School school : School.values()) {
            if (school.schoolName.equals(value) || school.name().equals(value)) {
                return school;
            }
        }
        throw new IllegalArgumentException("Unknown school: " + value);
    }
}

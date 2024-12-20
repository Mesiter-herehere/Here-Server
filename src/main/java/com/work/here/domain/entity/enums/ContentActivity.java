package com.work.here.domain.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ContentActivity {

    GENERAL("일반 컨텐츠"),
    FLAGGED("선정성이 있는 컨텐츠");
//    RESTRICTED("제한된 컨텐츠");

    private final String description;

}

package com.work.here.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportedDto {

    private Long selfIntroId;
    private String selfIntroTitle;
    private String reportedUserEmail;
    private String reportedUserName;
    private String reporterEmail;
    private String reporterName;
    private String reason;
}

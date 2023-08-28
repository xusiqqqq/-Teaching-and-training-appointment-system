package com.kclm.xsap.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper=false)
public class MemberCardDTO {
    private Long cardId;
    private String name;
    private String type;
    private Integer totalCount;
    private LocalDate endDate;
    private int status;


}

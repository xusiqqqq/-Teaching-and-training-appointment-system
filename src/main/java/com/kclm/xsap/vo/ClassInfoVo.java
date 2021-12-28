package com.kclm.xsap.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author fangkai
 * @description
 * @create 2021-12-18 16:44
 */

@Data
@Accessors(chain = true)
public class ClassInfoVo {
    /*
    courseName"},
classTime"},
teacherName"},
cardName"},
classNumbers"}
timesCost"},
comment"},
checkStatus",
     */
    private Long classRecordId;

    private String courseName;

//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime classTime;
    private String teacherName;
    private String cardName;
    private Integer classNumbers;
    private Integer timesCost;
    private String comment;
    private Integer checkStatus;

    private LocalDate scheduleStartDate;
    private LocalTime scheduleStartTime;
}

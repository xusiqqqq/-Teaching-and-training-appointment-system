package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class ScheduleAddValidVo {
    private Long scheduleId;
    private Long courseId;
    private Integer duration;
    private Long teacherId;
    private LocalTime classTime;
}

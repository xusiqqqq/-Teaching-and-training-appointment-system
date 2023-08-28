package com.kclm.xsap.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.vo.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ScheduleRecordService extends IService<ScheduleRecordEntity> {

    List<Map<String, String>> getReserveMapsByScheduleId(Long scheduleId);

    List<Map<String, String>> getAllReserveMapsByScheduleId(Long scheduleId);

    List<ClassRecordVo> getClassRecordVosByScheduleId(Long scheduleId);

    ScheduleDetailsVo getScheduleDetailsVo(Long id);

    List<ScheduleForConsumeSearchVo> getScheduleForConsumeSearchVo(LocalDateTime currentTime);

    List<ScheduleAddValidVo> getScheduleAddValidVo(ScheduleRecordEntity scheduleRecord);


    BigDecimal getAllCostByBindId(Long bindId);
    boolean consume(ConsumeFormVo consumeFormVo);

    public BigDecimal getAmountsPayable(Long bindCardId);

    Integer getCourseCostTimes(Long scheduleId);

    CourseEntity getCourseByScheduleId(Long scheduleId);

    public List<ScheduleRecordEntity> getScheduleRecordEntities(LocalDate targetDate, List<ScheduleRecordEntity> list);


}

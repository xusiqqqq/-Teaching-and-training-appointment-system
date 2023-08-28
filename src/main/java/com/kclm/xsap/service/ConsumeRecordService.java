package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.vo.TeacherClassCostVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface ConsumeRecordService extends IService<ConsumeRecordEntity> {
    BigDecimal getAllCostByBindId(Long bindId);
    Integer getCostTimesByBindId(Long bindId);

    List<TeacherClassCostVo> getTeacherClassCostVo(LocalDateTime start,LocalDateTime end);

    Integer getClassTimeCounts(LocalDateTime start,LocalDateTime end);


}

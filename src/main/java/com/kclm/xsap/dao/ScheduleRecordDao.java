package com.kclm.xsap.dao;

import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 中间表：排课计划表
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface ScheduleRecordDao extends BaseMapper<ScheduleRecordEntity> {

    /**
     * 获取指定日期的所有排课
     * @param startDate
     * @return
     */
    List<ScheduleRecordEntity> selectSameDateSchedule(@Param("startDate") LocalDate startDate);
}

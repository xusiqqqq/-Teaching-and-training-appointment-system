package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.kclm.xsap.utils.PageUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 中间表：排课计划表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface ScheduleRecordService extends IService<ScheduleRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ScheduleRecordEntity> getSameDateSchedule(LocalDate startDate);
}


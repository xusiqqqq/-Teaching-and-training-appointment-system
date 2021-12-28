package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.ScheduleRecordDao;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.kclm.xsap.service.ScheduleRecordService;


@Service("scheduleRecordService")
public class ScheduleRecordServiceImpl extends ServiceImpl<ScheduleRecordDao, ScheduleRecordEntity> implements ScheduleRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ScheduleRecordEntity> page = this.page(
                new Query<ScheduleRecordEntity>().getPage(params),
                new QueryWrapper<ScheduleRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ScheduleRecordEntity> getSameDateSchedule(LocalDate startDate) {

        return baseMapper.selectSameDateSchedule(startDate);
    }

}
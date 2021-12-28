package com.kclm.xsap.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.ReservationRecordDao;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.service.ReservationRecordService;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.ScheduleDetailReservedVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("reservationRecordService")
public class ReservationRecordServiceImpl extends ServiceImpl<ReservationRecordDao, ReservationRecordEntity> implements ReservationRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ReservationRecordEntity> page = this.page(
                new Query<ReservationRecordEntity>().getPage(params),
                new QueryWrapper<ReservationRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer getActiveUserCount() {

        //获取当前时间
        DateTime now = DateTime.now();
        log.debug("当前时间：{}", now);
        //获取一个月前时间
        DateTime aMonthAgo = now.offsetNew(DateField.DAY_OF_MONTH, -30);
        log.debug("一个月之前时间：{}", aMonthAgo);


        QueryWrapper<ReservationRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).ge("create_time", aMonthAgo.toString("yyyy-MM-dd")).le("create_time", now.toString("yyyy-MM-dd"));

        int activeUserCount = this.count(wrapper);

        log.debug("活跃用户：{}", activeUserCount);

        return activeUserCount;
    }

    @Override
    public List<ScheduleDetailReservedVo> getReserveList(Long id, String flag) {

        return baseMapper.selectReserveList(id, flag);
    }

    @Override
    public List<ReservationRecordEntity> getReserveInfo(Long id) {
        return baseMapper.selectReserveInfo(id);
    }


}
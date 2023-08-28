package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.RechargeRecordDao;
import com.kclm.xsap.entity.RechargeRecordEntity;
import com.kclm.xsap.service.RechargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service("rechargeRecordService")
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordDao,RechargeRecordEntity> implements RechargeRecordService {
    @Resource
    private RechargeRecordDao rechargeDao;

    @Override
    public LocalDateTime getOldestTime() {
        return rechargeDao.getEarlyCreatTime();
    }

    @Override
    public BigDecimal getChargeByStartAndEnd(LocalDateTime startTime, LocalDateTime endTime) {
        return rechargeDao.getChargeCountByDay(startTime, endTime)==null?new BigDecimal(0):rechargeDao.getChargeCountByDay(startTime, endTime);
    }
}

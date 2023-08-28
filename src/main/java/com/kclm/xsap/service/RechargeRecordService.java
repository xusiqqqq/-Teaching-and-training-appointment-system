package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.RechargeRecordEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RechargeRecordService extends IService<RechargeRecordEntity> {

    LocalDateTime getOldestTime();
    BigDecimal getChargeByStartAndEnd(LocalDateTime startTime,LocalDateTime endTime);

}

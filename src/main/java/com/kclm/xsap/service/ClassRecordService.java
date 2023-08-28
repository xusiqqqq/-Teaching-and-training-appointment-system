package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ClassRecordEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;


public interface ClassRecordService extends IService<ClassRecordEntity> {
    ReservationRecordEntity getReserveByClassRecord(ClassRecordEntity classRecord);
}

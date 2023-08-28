package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.ClassRecordDao;
import com.kclm.xsap.entity.ClassRecordEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service("classRecordService")
public class ClassRecordServiceImpl extends ServiceImpl<ClassRecordDao,ClassRecordEntity> implements ClassRecordService {
    @Resource
    private ClassRecordDao dao;

    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private GlobalReservationSetService globalReservationSetService;

    @Resource
    private CourseService courseService;

    @Resource
    private ScheduleRecordService scheduleRecordService;

    @Resource
    private MemberCardService memberCardService;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Override
    public ReservationRecordEntity getReserveByClassRecord(ClassRecordEntity classRecord) {
        LambdaQueryWrapper<ReservationRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getScheduleId,classRecord.getScheduleId())
                .eq(ReservationRecordEntity::getMemberId,classRecord.getMemberId())
                .eq(ReservationRecordEntity::getCardId,memberBindRecordService.getById(classRecord.getBindCardId()).getCardId())
                .eq(ReservationRecordEntity::getStatus,1);
        return reservationService.getOne(qw);
    }
}

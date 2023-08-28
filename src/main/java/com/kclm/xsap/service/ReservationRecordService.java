package com.kclm.xsap.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.utils.exception.ReserveAddException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface ReservationRecordService extends IService<ReservationRecordEntity> {
    //通过memberId，查出会员的所有预约记录
    List<Map<String,String>> getReserveByMemberId(Long memberId);
    //通过scheduleId，查出预约状态为1的所有预约记录
    List<Map<String, String>> getReserveMapsByScheduleId(Long scheduleId);

    //通过scheduleId，查出所有的预约数据
    List<Map<String, String>> getAllReserveMapsByScheduleId(Long scheduleId);

    void validReserveTime(LocalDateTime currentTime, GlobalReservationSetEntity globalReservationSet, LocalDateTime courseStartTime);

    void  validReserveCard(CourseEntity course, MemberCardEntity card, MemberBindRecordEntity bindRecord, LocalDateTime courseStartTime);

    void validReserveCourse(ReservationRecordEntity reservationRecord, LocalDateTime currentTime, ScheduleRecordEntity scheduleRecord, CourseEntity course, MemberEntity member);

    void validReserveMember(ReservationRecordEntity reservationRecord);
}

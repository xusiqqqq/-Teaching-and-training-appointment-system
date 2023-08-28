package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.service.GlobalReservationSetService;
import com.kclm.xsap.service.ReservationRecordService;
import com.kclm.xsap.service.ScheduleRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;


@SpringBootTest
public class ReservationRecord {
    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private GlobalReservationSetService globalReservationSetService;

    @Resource
    private CourseService courseService;

    @Resource
    private ScheduleRecordService scheduleRecordService;

    //课程预约
    @Test
    public void test() {
        ReservationRecordEntity reservationRecord=new ReservationRecordEntity();
//                scheduleId: 135
//        operator: 张三
//        memberId: 85
//        cardName: 测试卡
//        cardId: 51
//        reserveNums: 1
        reservationRecord.setScheduleId(135l);
        reservationRecord.setOperator("涨三");
        reservationRecord.setMemberId(85l);
        reservationRecord.setCardName("萧炎");
        reservationRecord.setCardId(51l);
        reservationRecord.setReserveNums(1);

        //获取当前日期时间
        LocalDateTime currentTime = LocalDateTime.now();
        reservationRecord.setCreateTime(currentTime);
        //首先读取获取预约全局配置
        GlobalReservationSetEntity globalReservationSet = globalReservationSetService.list().get(0);

        ScheduleRecordEntity scheduleRecord = scheduleRecordService.getById(reservationRecord.getScheduleId());
        //取消预约次数有没有设置，若有，查出该用户对该门课取消预约的次数   需注意取消预约次数过多 不能预约
        //获取课程可以最多取消的次数
        if(scheduleRecord!=null){
            CourseEntity course = courseService.getById(scheduleRecord.getCourseId());
            if (course.getLimitCounts() > 0) {
                LambdaQueryWrapper<ReservationRecordEntity> qw1 = new LambdaQueryWrapper<>();
                qw1.eq(ReservationRecordEntity::getMemberId, reservationRecord.getMemberId()).eq(ReservationRecordEntity::getStatus, 0);
                int cancelCount = reservationService.list(qw1).size();
//            if (cancelCount >= course.getLimitCounts())
//                return R.error("由于您对此取消该课程预约，现已无法进行预约此课程");
            }
        }




        //预约开始时间有没有设置，若有，需要注意要在预约开始之后才能预约
        //预约截止时间有没有设置，若有，需注意currentTime需要在截止日期之前。合并上条，也就是预约时间需要在开始和截止之前
        //有设置预约开始时间
        if (globalReservationSet.getAppointmentStartMode() != 1 || globalReservationSet.getAppointmentDeadlineMode() != 1) {
            //课程开始时间
            LocalDateTime courseStartTime = LocalDateTime.of(scheduleRecord.getStartDate(), scheduleRecord.getClassTime());

            System.out.println("courseStartTime"+courseStartTime);

            //预约开始时间=课程开始时间-设置的开始预约天数/null
            LocalDateTime reserveStartTime = courseStartTime.plusDays(-globalReservationSet.getStartDay());
            //预约结束时间=课程开始时间-设置的的提前截止时间/null
            LocalDateTime reserveEndTime = null;
            if (globalReservationSet.getAppointmentDeadlineMode() == 2) {
                Integer endHour = globalReservationSet.getEndHour();
                reserveEndTime = courseStartTime.plusHours(-endHour);
            }
            if (globalReservationSet.getAppointmentDeadlineMode() == 3) {
                Integer endDay = globalReservationSet.getEndDay();
                System.out.println("endDay:"+endDay);
                LocalTime endTime = globalReservationSet.getEndTime();
                System.out.println("endTime"+endTime);
                reserveEndTime=courseStartTime.plusDays(-endDay).plusHours(-endTime.getHour()).plusMinutes(-endTime.getMinute());


            }
            System.out.println("reserveStartTime"+reserveStartTime);
            System.out.println("reserveEndTime"+reserveEndTime);


        }
    }
}

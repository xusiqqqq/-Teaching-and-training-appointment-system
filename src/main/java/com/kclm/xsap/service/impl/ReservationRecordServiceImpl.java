package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.ReservationRecordDao;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.utils.exception.ReserveAddException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("reservationRecordService")
public class ReservationRecordServiceImpl extends ServiceImpl<ReservationRecordDao,ReservationRecordEntity> implements ReservationRecordService {
    @Resource
    private ReservationRecordDao dao;

    @Resource
    private ScheduleRecordService scheduleService;

    @Resource
    private MemberService memberService;

    @Resource
    private CourseService courseService;

    @Resource
    private CourseCardService courseCardService;

    @Resource
    private EmployeeService employeeService;



    //查出预约状态为1预约记录
    @Override
    public List<Map<String, String>> getReserveMapsByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getScheduleId, scheduleId)
                .eq(ReservationRecordEntity::getStatus,1);
        List<ReservationRecordEntity> list = dao.selectList(qw);
        return getMapsByScheduleId(scheduleId, list);
    }

    //查出所有的预约数据
    @Override
    public List<Map<String, String>> getAllReserveMapsByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getScheduleId, scheduleId);
        List<ReservationRecordEntity> list = dao.selectList(qw);
        return getMapsByScheduleId(scheduleId,list);
    }

    @Override
    public void validReserveTime(LocalDateTime currentTime, GlobalReservationSetEntity globalReservationSet, LocalDateTime courseStartTime)  {
        //如果课程开始时间在current之前，不能进行预约
        if(courseStartTime.isBefore(currentTime)){
            throw  new ReserveAddException(R.error("该课程已经开始/结束，您无法进行预约"));
        }

        //预约开始时间有没有设置，预约时间需要在开始和截止之前
        if(globalReservationSet.getAppointmentStartMode()!=1|| globalReservationSet.getAppointmentDeadlineMode()!=1){
            //预约开始时间=课程开始时间-设置的开始预约天数/null
            LocalDateTime reserveStartTime= courseStartTime.plusDays(-globalReservationSet.getStartDay());
            //预约结束时间=课程开始时间-设置的的提前截止时间/null
            LocalDateTime reserveEndTime= courseStartTime;
            if(globalReservationSet.getAppointmentDeadlineMode()==2){
                Integer endHour = globalReservationSet.getEndHour();
                reserveEndTime= courseStartTime.plusHours(-endHour);
            }
            if(globalReservationSet.getAppointmentDeadlineMode()==3){
                Integer endDay = globalReservationSet.getEndDay();
                LocalTime endTime = globalReservationSet.getEndTime();
                reserveEndTime= courseStartTime.plusDays(-endDay).plusHours(-endTime.getHour()).plusMinutes(-endTime.getMinute());
            }
            if(currentTime.isBefore(reserveStartTime)){
                throw new ReserveAddException(R.error("课程预约还没开始，请您耐心等待")) ;
            }
            if(currentTime.isAfter(reserveEndTime)){
                throw new ReserveAddException("课程预约已经结束");
            }
        }
    }

    @Override
    public void validReserveCard(CourseEntity course, MemberCardEntity card, MemberBindRecordEntity bindRecord, LocalDateTime courseStartTime)  {
        //当前卡能否上这门课
        List<Long> supportCardsId = courseCardService.getCardsIdByCourseId(course.getId());
        if(!supportCardsId.contains(card.getId())) throw new ReserveAddException(R.error("您的卡不支持上该门课程"));
        //当前卡没有过期(过期：应该是课程结束后过期的才能预约，如果在结束前过期，将不能扣钱）
        LocalDateTime cardValidTime= bindRecord.getCreateTime().plusDays(bindRecord.getValidDay());
        if(cardValidTime.isBefore(courseStartTime)){
            throw new ReserveAddException(R.error("您的会员卡在该课程开始前就会过期，无法支付"));
        }
        //当前卡的有效次数不得小于课程所需耗费的次数,并且
        if(bindRecord.getValidCount()< course.getTimesCost()){
            throw new ReserveAddException(R.error("您的卡剩余次数无法支付该课程所需要的次数，请充值之后再预约"));
        }
    }

    @Override
    public void validReserveCourse(ReservationRecordEntity reservationRecord, LocalDateTime currentTime, ScheduleRecordEntity scheduleRecord, CourseEntity course, MemberEntity member)  {

        //预约人数不得大于课堂剩余的空人数
        if(reservationRecord.getReserveNums()>(course.getContains()- scheduleRecord.getOrderNums()))
            throw new ReserveAddException("预约人数超过课堂容纳剩余人数，预约失败");

        //取消预约次数有没有设置，若有，查出该用户对该门课取消预约的次数   需注意取消预约次数过多 不能预约
        if(course.getLimitCounts()>0){
            LambdaQueryWrapper<ReservationRecordEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(ReservationRecordEntity::getMemberId, reservationRecord.getMemberId())
                    .eq(ReservationRecordEntity::getScheduleId, scheduleRecord.getId())
                    .eq(ReservationRecordEntity::getStatus,0)
                    .eq(ReservationRecordEntity::getCancelTimes,1);
            //已经取消的次数与限制的次数进行比较
            if(dao.selectCount(qw)>= course.getLimitCounts())
                throw new ReserveAddException("由于您多次取消该课程预约，现已无法预约此课程");
        }
        //添加预约时，要判断会员的性别和年龄
        //年龄   年龄无限制时，默认为0
        Integer memberAge= member.getBirthday().until(currentTime.toLocalDate()).getYears();
        if(course.getLimitAge()>memberAge)
            throw new ReserveAddException("会员年龄太小了，还不能上这门课");
        //性别   性别无限制时，为无限制
        if(!course.getLimitSex().equals("无限制")&&!course.getLimitSex().equals(member.getSex()))
            throw new ReserveAddException("抱歉，这门课只支持" + course.getLimitSex() + "性");
    }

    @Override
    public void validReserveMember(ReservationRecordEntity reservationRecord) {
        //如果用户已经预约过该课程，则进行提醒
        LambdaQueryWrapper<ReservationRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getMemberId, reservationRecord.getMemberId())
                .eq(ReservationRecordEntity::getScheduleId, reservationRecord.getScheduleId())
                .eq(ReservationRecordEntity::getStatus,1);
        if(dao.selectCount(qw)>0)
            throw new ReserveAddException(R.error("您已经预约了该课程，请勿重复预约"));
    }

    @Override
    public List<Map<String, String>> getReserveByMemberId(Long memberId) {
        LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getMemberId,memberId);
        List<ReservationRecordEntity> list = dao.selectList(qw);
        //课程	预约时间	会员卡	预约人数	使用次数	操作时间	操作人	预约备注	预约类型
        List<Map<String,String>> mapList=new ArrayList<>();
        if(list!=null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (ReservationRecordEntity reservation : list) {
                Map<String,String> map=new HashMap<>();
                ScheduleRecordEntity schedule = scheduleService.getById(reservation.getScheduleId());
                CourseEntity course = courseService.getById(schedule.getCourseId());
                map.put("courseName",course.getName());

                map.put("reserveTime",reservation.getCreateTime().format(dtf));
                map.put("cardName",reservation.getCardName());
                map.put("reserveNumbers",reservation.getReserveNums().toString());
                map.put("timesCost",course.getTimesCost().toString());
                if(reservation.getCreateTime()!=null)
                    map.put("operateTime", reservation.getCreateTime().format(dtf));
                map.put("operator",reservation.getOperator());
                map.put("reserveNote",reservation.getNote());
                map.put("reserveStatus",reservation.getStatus().toString());
                mapList.add(map);
            }

        }
        return mapList;
    }
    private List<Map<String, String>> getMapsByScheduleId(Long scheduleId, List<ReservationRecordEntity> list) {
        List<Map<String,String>> mapList=new ArrayList<>();
        for (ReservationRecordEntity reservationRecord : list) {
            Map<String,String> map=new HashMap<>();
            MemberEntity member = memberService.getById(reservationRecord.getMemberId());
            map.put("reserveId",reservationRecord.getId().toString());
            map.put("memberName",member.getName());
            map.put("phone",member.getPhone());
            map.put("cardName",reservationRecord.getCardName());
            map.put("reserveNumbers",reservationRecord.getReserveNums().toString());
            map.put("timesCost",courseService.getCourseByScheduleId(scheduleId).getTimesCost().toString());
            if(reservationRecord.getCreateTime()!=null)
                map.put("operateTime", reservationRecord.getCreateTime().toString());
            map.put("operator",reservationRecord.getOperator());
            map.put("reserveNote",reservationRecord.getNote());
            map.put("reserveStatus",reservationRecord.getStatus().toString());
            mapList.add(map);
        }
        return mapList;
    }
}

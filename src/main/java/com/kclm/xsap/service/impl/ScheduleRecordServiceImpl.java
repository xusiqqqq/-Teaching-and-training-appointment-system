package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.*;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("scheduleRecordService")
public class ScheduleRecordServiceImpl extends ServiceImpl<ScheduleRecordDao,ScheduleRecordEntity> implements ScheduleRecordService {
    @Resource
    private ScheduleRecordDao dao;

    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private MemberService memberService;

    @Resource
    private CourseService courseService;

    @Resource
    private CourseCardService courseCardService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private ClassRecordService classService;
    @Resource
    private ConsumeRecordDao consumeDao;

    @Resource
    private GlobalReservationSetDao globalSetDao;

    @Resource
    private ScheduleRecordDao scheduleDao;

    @Resource
    private MemberBindRecordDao bindRecordDao;

    @Resource
    private MemberLogDao memberLogDao;


    //查出状态为1的并且在课程开始时间之前的预约记录
    @Override
    public List<Map<String, String>> getReserveMapsByScheduleId(Long scheduleId) {
        ScheduleRecordEntity scheduleRecord = scheduleDao.selectById(scheduleId);
        LocalDateTime courseStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());
        List<ReservationRecordEntity> list =new ArrayList<>();
        if(LocalDateTime.now().isBefore(courseStartTime)){
            LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
            qw.eq(ReservationRecordEntity::getScheduleId, scheduleId)
                    .eq(ReservationRecordEntity::getStatus,1);
            list = reservationService.list(qw);
        }
        return getMaps(scheduleId, list);
    }

    //查出所有的预约数据
    @Override
    public List<Map<String, String>> getAllReserveMapsByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getScheduleId, scheduleId);
        List<ReservationRecordEntity> list = reservationService.list(qw);
        return getMaps(scheduleId,list);
    }

    @Override
    public List<ClassRecordVo> getClassRecordVosByScheduleId(Long scheduleId) {
        //获取schedule

        //查出该门课程的预约状态为1的所有ClassRecord
        LambdaQueryWrapper<ClassRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ClassRecordEntity::getScheduleId,scheduleId).eq(ClassRecordEntity::getReserveCheck,1);
        List<ClassRecordEntity> classList = classService.list(qw);
        List<ClassRecordVo> classRecordVos=new ArrayList<>();
        if(classList!=null){

            for (ClassRecordEntity classRecord : classList) {
                //需要查出当前的会员
                LambdaQueryWrapper<ReservationRecordEntity> qw2=new LambdaQueryWrapper<>();
                MemberEntity member = memberService.getById(classRecord.getMemberId());
                qw2.eq(ReservationRecordEntity::getScheduleId,scheduleId)
                        .eq(ReservationRecordEntity::getMemberId,member.getId())
                        .eq(ReservationRecordEntity::getStatus,1);
                ReservationRecordEntity reservation = reservationService.getOne(qw2);
                ClassRecordVo vo=new ClassRecordVo();
                vo.setClassRecordId(classRecord.getId());
                vo.setCardName(classRecord.getCardName());
                vo.setTimesCost(courseService.getCourseByScheduleId(classRecord.getScheduleId()).getTimesCost());
                vo.setReserveNums(reservation.getReserveNums());
                vo.setOperateTime(reservation.getCreateTime());
                vo.setCardId(classRecord.getBindCardId());
                vo.setCheckStatus(classRecord.getCheckStatus());
                vo.setMemberId(member.getId());
                vo.setMemberName(member.getName());
                vo.setMemberPhone(member.getPhone());
                vo.setMemberSex(member.getSex());
                vo.setMemberBirthday(member.getBirthday());
                classRecordVos.add(vo);
            }
        }
        return classRecordVos;
    }

    //根据scheduleId查出课程的详细信息
    @Override
    public ScheduleDetailsVo getScheduleDetailsVo(Long id)  {
        ScheduleRecordEntity schedule = dao.selectById(id);
        CourseEntity course=courseService.getById(schedule.getCourseId());
        ScheduleDetailsVo vo=new ScheduleDetailsVo();
        vo.setCourseName(course.getName());
        LocalDateTime start = LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime());
        LocalDateTime end=start.plusMinutes(course.getDuration());
        vo.setStartTime(start);
        vo.setEndTime(end);
        vo.setDuration(course.getDuration());
        vo.setLimitAge(course.getLimitAge());
        vo.setLimitSex(course.getLimitSex());
        String[] cardsName = courseCardService.getCardsNameByCourseId(course.getId());
        List<String> cardNameList=new ArrayList<>();
        for (String cardName : cardsName) {
            cardName="「"+cardName+"」";
            cardNameList.add(cardName);
        }
        vo.setSupportCards(cardNameList);
        vo.setTeacherName(employeeService.getTeacherNameById(schedule.getTeacherId()));
        vo.setOrderNums(schedule.getOrderNums());
        vo.setClassNumbers(course.getContains());
        vo.setTimesCost(course.getTimesCost());
        return vo;
    }

    @Override
    public List<ScheduleForConsumeSearchVo> getScheduleForConsumeSearchVo(LocalDateTime currentTime) {
        return dao.getScheduleForConsumeSearchVo(currentTime.plusWeeks(-2),currentTime);
    }

    @Override
    public List<ScheduleAddValidVo> getScheduleAddValidVo(ScheduleRecordEntity scheduleRecord) {
        LocalDateTime classStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());
        LocalDateTime earlyNinetyMinutes=classStartTime.minusMinutes(180);
        LocalDateTime lateNinetyMinutes=classStartTime.plusMinutes(scheduleRecord.getCourseDuration());
        return dao.getScheduleAddValidVo(scheduleRecord.getTeacherId(),earlyNinetyMinutes,lateNinetyMinutes);
    }

    @Override
    public BigDecimal getAllCostByBindId(Long bindId) {
        return consumeDao.getCountsMoneyByBindId(bindId);
    }

    @Override
    public boolean consume(ConsumeFormVo consumeFormVo)  {
        LocalDateTime currentTime=LocalDateTime.now();
        MemberBindRecordEntity bindRecord = bindRecordDao.selectById(consumeFormVo.getCardBindId());
        bindRecord.setValidCount(bindRecord.getValidCount()- consumeFormVo.getCardCountChange());
        bindRecord.setLastModifyTime(currentTime);
        int updateBind = bindRecordDao.updateById(bindRecord);
        MemberLogEntity memberLog=new MemberLogEntity();
        memberLog.setType("会员上课扣费");
        memberLog.setInvolveMoney(consumeFormVo.getAmountOfConsumption());
        memberLog.setMemberBindId(consumeFormVo.getCardBindId());
        memberLog.setCreateTime(currentTime);
        memberLog.setCardCountChange(consumeFormVo.getCardCountChange());
        memberLog.setCardActiveStatus(1);
        memberLog.setOperator(consumeFormVo.getOperator());
        int insertLog = memberLogDao.insert(memberLog);
        ConsumeRecordEntity consumeRecord=new ConsumeRecordEntity();
        consumeRecord.setOperateType("会员正常预约上课扣费");
        consumeRecord.setCardCountChange(consumeFormVo.getCardCountChange());
        consumeRecord.setMoneyCost(consumeFormVo.getAmountOfConsumption());
        consumeRecord.setOperator(consumeFormVo.getOperator());
        consumeRecord.setNote(consumeFormVo.getNote());
        consumeRecord.setMemberBindId(consumeFormVo.getCardBindId());
        consumeRecord.setCreateTime(currentTime);
        consumeRecord.setScheduleId(consumeFormVo.getScheduleId());
        consumeRecord.setLogId(memberLog.getId());
        int insertConsume = consumeDao.insert(consumeRecord);
        //改classRecord表中的状态
        ClassRecordEntity classRecord = classService.getById(consumeFormVo.getClassId());
        classRecord.setCheckStatus(1);
        classRecord.setLastModifyTime(currentTime);
        boolean b4=classService.updateById(classRecord);
        return updateBind==1&&insertLog==1&&insertConsume==1&&b4;

    }

    @Override
    public BigDecimal getAmountsPayable(Long bindCardId) {
        MemberBindRecordEntity bindRecord = bindRecordDao.selectById(bindCardId);
        Integer validCount = bindRecord.getValidCount();
        //再通过bindId查出这张卡的消费记录
        BigDecimal receivedMoney = bindRecord.getReceivedMoney();
        BigDecimal allCostByBindId =(consumeDao.getCountsMoneyByBindId(bindCardId)==null? BigDecimal.valueOf(0) :consumeDao.getCountsMoneyByBindId(bindCardId)) ;
        BigDecimal realCounts=receivedMoney.subtract(allCostByBindId);
        return realCounts.divide(BigDecimal.valueOf(validCount),0, RoundingMode.DOWN);
    }

    @Override
    public Integer getCourseCostTimes(Long scheduleId) {
        return courseService.getById(scheduleDao.selectById(scheduleId).getCourseId()).getTimesCost();
    }

    @Override
    public CourseEntity getCourseByScheduleId(Long scheduleId) {
        ScheduleRecordEntity scheduleRecordEntity = scheduleDao.selectById(scheduleId);
        return courseService.getById(scheduleRecordEntity.getCourseId());
    }

    //如果有冲突课程，通过要插入的课程，去算出这老师在目标日期那天上的课，若复制的课和原有的课存在冲突，则需要剔除复制中的相关课程，并记录数据
    @Override
    public List<ScheduleRecordEntity> getScheduleRecordEntities(LocalDate targetDate, List<ScheduleRecordEntity> list)  {
        List<ScheduleRecordEntity> resultList=new ArrayList<>();
        for (ScheduleRecordEntity addSchedule : list) {
            CourseEntity sCourse = courseService.getById(addSchedule.getCourseId());
            addSchedule.setStartDate(targetDate);
            //复制列表中每一门要添加的课程，其上课老师在目标日期也在上的所有课
            LambdaQueryWrapper<ScheduleRecordEntity> qw2=new LambdaQueryWrapper<>();
            qw2.eq(ScheduleRecordEntity::getTeacherId,addSchedule.getTeacherId())
                    .eq(ScheduleRecordEntity::getStartDate, targetDate);
            List<ScheduleRecordEntity> list2 = scheduleDao.selectList(qw2);
            boolean flag=true;
            if(!list2.isEmpty()){
                LocalTime classStartTime=addSchedule.getClassTime();
                LocalTime classEndTime=classStartTime.plusMinutes(sCourse.getDuration());
                flag = isScheduleConflict(list2, true, classStartTime, classEndTime);
            }
            if(flag) resultList.add(addSchedule);
        }
        return resultList;
    }

    private List<Map<String, String>> getMaps(Long scheduleId, List<ReservationRecordEntity> list) {
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

    //判断同一个老师，同一天的课是否有冲突
    private boolean isScheduleConflict(List<ScheduleRecordEntity> list2, boolean flag, LocalTime classStartTime, LocalTime classEndTime) {
        for (ScheduleRecordEntity existSchedule : list2) {
            LocalTime existClassStartTime=existSchedule.getClassTime();
            LocalTime existClassEndTime=existClassStartTime.plusMinutes(courseService.getById(existSchedule.getCourseId()).getDuration());
            //1、要添加的数据，开始时间，结束时间中存在已经有的课程的开始时间，则冲突
            if(classStartTime.isBefore(existClassStartTime)&& classEndTime.isAfter(existClassStartTime)){
                flag =false;
                break;
            }
            //2、要添加的数据，开始时间一致
            if(classStartTime.equals(existClassStartTime)){
                flag =false;
                break;
            }
            //3、要添加的排课，其开始时间 在 存在的课程开始时间和结束时间之内
            if(classStartTime.isAfter(existClassStartTime)&& classStartTime.isBefore(existClassEndTime)){
                flag =false;
                break;
            }
        }
        return flag;
    }
}

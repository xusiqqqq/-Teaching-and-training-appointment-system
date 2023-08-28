package com.kclm.xsap.web.controller;

import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.MemberCardStatisticsVo;
import com.kclm.xsap.vo.MemberCardStatisticsWithTotalDataInfoVo;
import com.kclm.xsap.vo.TeacherClassCostVo;
import com.kclm.xsap.vo.statistics.CardCostVo;
import com.kclm.xsap.vo.statistics.ClassCostVo;
import com.kclm.xsap.vo.statistics.StatisticsOfCardCostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    static final int MONTH=1;
    static final int SEASON=2;
    static final int YEAR=3;
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
    private MemberService memberService;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource
    private ClassRecordService classRecordService;

    @Resource
    private ConsumeRecordService consumeService;

    @Resource
    private RechargeRecordService rechargeService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private StatisticsService statisticsService;


    /**
     * 会员卡信息
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/cardInfo.do")
    public R getCardInfo(){
        //总课时
        Integer totalCourseTimeAll=0;
        //已用课时
        Integer usedCourseTimeAll=0;
        //剩余课时
        Integer remainCourseTimeAll=0;
        //总金额
        BigDecimal totalMoneyAll  =new BigDecimal(0);
        //已用金额
        BigDecimal usedMoneyAll   =new BigDecimal(0);
        //剩余金额
        BigDecimal remainMoneyAll=new BigDecimal(0);
        List<MemberBindRecordEntity> bindList = memberBindRecordService.getValidMemberBindList();
        List<MemberCardStatisticsVo> voList=new ArrayList<>();
        for (MemberBindRecordEntity bindRecord : bindList) {
            MemberCardStatisticsVo vo=new MemberCardStatisticsVo();
            vo.setMemberId(bindRecord.getMemberId());
            vo.setMemberName(memberService.getById(bindRecord.getMemberId()).getName());
            vo.setBindCardId(bindRecord.getId());
            //总金额
            vo.setLumpSumBigD(bindRecord.getReceivedMoney());
            vo.setLumpSum(bindRecord.getReceivedMoney().toString());
            //已用金额
            BigDecimal allCostByBindId = consumeService.getAllCostByBindId(bindRecord.getId());
            vo.setAmountUsedBigD(allCostByBindId);
            vo.setAmountUsed(allCostByBindId.toString());
            //剩余金额
            vo.setBalanceBigD(vo.getLumpSumBigD().subtract(allCostByBindId));
            vo.setBalance(vo.getBalanceBigD().toString());
            //剩余课次
            vo.setRemainingClassTimes(bindRecord.getValidCount());
            //已用课次
            vo.setUsedClassTimes(consumeService.getCostTimesByBindId(bindRecord.getId()));
            //总课次
            vo.setTotalClassTimes(vo.getRemainingClassTimes()+vo.getUsedClassTimes());
            voList.add(vo);

            totalCourseTimeAll+=vo.getTotalClassTimes();
            usedCourseTimeAll+=vo.getUsedClassTimes();
            remainCourseTimeAll+=vo.getRemainingClassTimes();
            totalMoneyAll=totalMoneyAll.add(vo.getLumpSumBigD());
            usedMoneyAll=usedMoneyAll.add(vo.getAmountUsedBigD());
            remainMoneyAll=remainMoneyAll.add(vo.getBalanceBigD());
        }
        MemberCardStatisticsWithTotalDataInfoVo infoVo=new MemberCardStatisticsWithTotalDataInfoVo();
        infoVo.setMemberCardStatisticsVos(voList);
        infoVo.setTotalCourseTimeAll(totalCourseTimeAll);
        infoVo.setRemainCourseTimeAll(remainCourseTimeAll);
        infoVo.setUsedCourseTimeAll(usedCourseTimeAll);
        infoVo.setTotalMoneyAll(totalMoneyAll);
        infoVo.setRemainMoneyAll(remainMoneyAll);
        infoVo.setUsedMoneyAll(usedMoneyAll);
        return R.ok().put("data",infoVo);
    }

    /**
     * 获取系统开始和现在年份
     * @return
     */
    @ResponseBody
    @PostMapping("/yearList")
    public R getYearList(){
        LocalDateTime currentTime=LocalDateTime.now();
        LocalDateTime oldestTime = rechargeService.getOldestTime();
        List<Integer> yearList=new ArrayList<>();
        for(int i=oldestTime.getYear();i<=currentTime.getYear();i++){
            yearList.add(i);
        }
        return R.ok().put("data",yearList);
    }

    /**
     * 获取消费信息
     * @param vo StatisticsOfCardCostVo
     * @return
     */
    @ResponseBody
    @PostMapping("/cardCostMonthOrSeasonOrYear")
    public R getCardCostMonthOrSeasonOrYear(StatisticsOfCardCostVo vo){
        LocalDateTime currentTime=LocalDateTime.now();
        CardCostVo resultVo=null;
        //获取一年中每月的收入
        if(vo.getUnit()==MONTH){
            resultVo=statisticsService.getCardCostMonthOrSeasonOrYearByUnit1(vo, currentTime);
        }
        //获取一年中每季度的收入
        if(vo.getUnit()==SEASON){
            resultVo=statisticsService.getCardCostMonthOrSeasonOrYearByUnit2(vo, currentTime);
        }
        //获取每年的收入情况
        if(vo.getUnit()==YEAR){
            resultVo = statisticsService.getCardCostMonthOrSeasonOrYearByUnit3(vo);
        }
        return R.ok().put("data",resultVo);
    }

    /**
     * 获取一段时间内所有老师的课消次数
     * @param vo StatisticsOfCardCostVo
     * @return
     */
    @ResponseBody
    @PostMapping("/classCostMonthOrSeasonOrYear")
    public R toClassCostMonthOrSeasonOrYear(StatisticsOfCardCostVo vo){
        LocalDateTime startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0,0);
        LocalDateTime endTime;
        LocalDateTime currentTime=LocalDateTime.now();

        //查询单个时间段里所有老师的课消总次数以及金额
        List<TeacherClassCostVo> teacherClassCostVo=new ArrayList<>();
        //返回结果
        ClassCostVo resultVo=new ClassCostVo();
        if(vo.getUnit()==MONTH){
            //收费模式
            resultVo.setTitle("老师课消次数月统计");
            resultVo.setXname("月");
            resultVo = statisticsService.getClassCostVoByMonth( startTime, currentTime, teacherClassCostVo,resultVo);
        }
        if(vo.getUnit()==SEASON){
            //收费模式
            resultVo.setTitle("老师课消次数月统计");
            resultVo.setXname("季度");
            resultVo= statisticsService.getClassCostVoBySeason( startTime, currentTime, teacherClassCostVo,resultVo);
        }
        if(vo.getUnit()==YEAR){
            resultVo.setTitle("老师课消次数年度统计");
            resultVo.setXname("年");
            startTime=LocalDateTime.of(vo.getBeginYear(),1,1,0,0,0);
            endTime=LocalDateTime.of(vo.getEndYear(),12,31,23,59,59);
            resultVo=statisticsService.getClassCostVoByYear(startTime,endTime,teacherClassCostVo,resultVo);
        }
        return R.ok().put("data",resultVo);
    }

    /**
     * 获取某个时间段类的课时数
     * @param vo StatisticsOfCardCostVo
     * @return
     */
    @ResponseBody
    @PostMapping("/classCountMonthOrSeasonOrYear")
    public R getClassCountMonthOrSeasonOrYear(StatisticsOfCardCostVo vo){
        LocalDateTime currentTime=LocalDateTime.now();
        CardCostVo resultVo=new CardCostVo();
        //获取一年中每月的课时数
        if(vo.getUnit()==MONTH){
            resultVo=statisticsService.getClassCountByUnit1(vo, currentTime);
        }
        //获取一年中每季度的收入
        if(vo.getUnit()==SEASON){
            resultVo=statisticsService.getClassCountByUnit2(vo, currentTime);
        }
        //获取每年的收入情况
        if(vo.getUnit()==YEAR){
            resultVo = statisticsService.getClassCountByUnit3(vo);
        }
        return R.ok().put("data",resultVo);
    }

    /**
     * 获取一段时间内用户新增与流失
     * @param vo StatisticsOfCardCostVo
     * @return
     */
    @ResponseBody
    @PostMapping("/addAndStreamCountMonthOrSeasonOrYear")
    public R getAddAndStreamCountMonthOrSeasonOrYear(StatisticsOfCardCostVo vo){
        LocalDateTime currentTime=LocalDateTime.now();
        CardCostVo resultVo=new CardCostVo();
        //获取一年中每月的用户新增与流失
        if(vo.getUnit()==MONTH){
            resultVo=statisticsService.getAddAndStreamCountByUnit1(vo, currentTime);
        }
        //获取一年中每季度的用户新增与流失
        if(vo.getUnit()==SEASON){
            resultVo=statisticsService.getAddAndStreamCountByUnit2(vo, currentTime);
        }
        //获取每年的用户新增与流失情况
        if(vo.getUnit()==YEAR){
            resultVo = statisticsService.getAddAndStreamCountByUnit3(vo);
        }
        return R.ok().put("data",resultVo);
    }
}

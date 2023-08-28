package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.service.ConsumeRecordService;
import com.kclm.xsap.service.MemberService;
import com.kclm.xsap.service.RechargeRecordService;
import com.kclm.xsap.service.StatisticsService;
import com.kclm.xsap.vo.TeacherClassCostVo;
import com.kclm.xsap.vo.statistics.CardCostVo;
import com.kclm.xsap.vo.statistics.ClassCostVo;
import com.kclm.xsap.vo.statistics.StatisticsOfCardCostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private RechargeRecordService rechargeService;

    @Resource
    private ConsumeRecordService consumeService;

    @Resource
    private MemberService memberService;
    @Override
    public CardCostVo getCardCostMonthOrSeasonOrYearByUnit3(StatisticsOfCardCostVo vo)  {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("年收费模式");
        startTime=LocalDateTime.of(vo.getBeginYear(),1,1,0,0);
        resultVo.setXname("年");
        for(int i = vo.getBeginYear(); i<= vo.getEndYear(); i++){
            endTime=startTime.plusYears(1);
            time.add(i+"年");
            data.add(rechargeService.getChargeByStartAndEnd(startTime,endTime).intValue());
            startTime=startTime.plusYears(1);
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public CardCostVo getCardCostMonthOrSeasonOrYearByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime endTime;
        LocalDateTime startTime;
        resultVo.setTitle("季度收费模式");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("季度");
        for(int i=1;i<=4;i++){
            endTime=startTime.plusMonths(3);
            time.add(i+"季度");
            data.add(rechargeService.getChargeByStartAndEnd(startTime,endTime).intValue());
            startTime=startTime.plusMonths(3);
            if(currentTime.isBefore(endTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public CardCostVo getCardCostMonthOrSeasonOrYearByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("月收费模式");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("月");
        for(int i=1;i<=12;i++){
            endTime=startTime.plusMonths(1);
            time.add(i+"月");
            data.add(rechargeService.getChargeByStartAndEnd(startTime, endTime).intValue());
            startTime=startTime.plusMonths(1);
            if(startTime.isAfter(currentTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public ClassCostVo getClassCostVoByMonth(LocalDateTime startTime, LocalDateTime currentTime, List<TeacherClassCostVo> teacherClassCostVo,ClassCostVo resultVo) {

        LocalDateTime endTime;
        //x轴名字
        List<String> time=new ArrayList<>();
        //老师名字
        List<String> tname=new ArrayList<>();
        //课消次数
        List<List<Integer>> data=new ArrayList<>();
        //课消金额
        List<List<Float>> data2=new ArrayList<>();
        //存储多个老师在多个阶段的课消总次数
        Map<Long,List<Integer>> map1=new HashMap<>();
        //存储多个老师在多个阶段的课消总金额
        Map<Long,List<Float>> map2=new HashMap<>();

        for (int i = 1; i <=12; i++) {
            //x轴数据
            time.add(String.valueOf(i));
            endTime=startTime.plusMonths(1);
            teacherClassCostVo = consumeService.getTeacherClassCostVo(startTime, endTime);
            if(i==1){
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    List<Integer> temp1 = new ArrayList<>();
                    List<Float> temp2 = new ArrayList<>();
                    temp1.add(costVo.getCountChange());
                    temp2.add(costVo.getMoneyCost());
                    map1.put(costVo.getId(), temp1);
                    map2.put(costVo.getId(), temp2);
                }
            }else {
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    map1.get(costVo.getId()).add(costVo.getCountChange());
                    map2.get(costVo.getId()).add(costVo.getMoneyCost());
                }
            }
            startTime = startTime.plusMonths(1);
            if(startTime.isAfter(currentTime)) break;
        }
        for (TeacherClassCostVo costVo : teacherClassCostVo) {
            tname.add(costVo.getName());
            data.add(map1.get(costVo.getId()));
            data2.add(map2.get(costVo.getId()));
        }
        resultVo.setTname(tname);
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }

    @Override
    public ClassCostVo getClassCostVoBySeason(LocalDateTime startTime, LocalDateTime currentTime, List<TeacherClassCostVo> teacherClassCostVo,ClassCostVo resultVo) {

        LocalDateTime endTime;
        //x轴名字
        List<String> time=new ArrayList<>();
        //老师名字
        List<String> tname=new ArrayList<>();
        //课消次数
        List<List<Integer>> data=new ArrayList<>();
        //课消金额
        List<List<Float>> data2=new ArrayList<>();
        //存储多个老师在多个阶段的课消总次数
        Map<Long,List<Integer>> map1=new HashMap<>();
        //存储多个老师在多个阶段的课消总金额
        Map<Long,List<Float>> map2=new HashMap<>();

        for (int i = 1; i <=4; i++) {
            //x轴数据
            time.add(String.valueOf(i));
            endTime=startTime.plusMonths(3);
            teacherClassCostVo = consumeService.getTeacherClassCostVo(startTime, endTime);
            if(i==1){
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    List<Integer> temp1 = new ArrayList<>();
                    List<Float> temp2 = new ArrayList<>();
                    temp1.add(costVo.getCountChange());
                    temp2.add(costVo.getMoneyCost());
                    map1.put(costVo.getId(), temp1);
                    map2.put(costVo.getId(), temp2);
                }
            }else {
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    map1.get(costVo.getId()).add(costVo.getCountChange());
                    map2.get(costVo.getId()).add(costVo.getMoneyCost());
                }
            }
            startTime=startTime.plusMonths(3);
            if(startTime.isAfter(currentTime)) break;
        }
        for (TeacherClassCostVo costVo : teacherClassCostVo) {
            tname.add(costVo.getName());
            data.add(map1.get(costVo.getId()));
            data2.add(map2.get(costVo.getId()));
        }
        resultVo.setTname(tname);
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }



    @Override
    public ClassCostVo getClassCostVoByYear(LocalDateTime start, LocalDateTime end, List<TeacherClassCostVo> teacherClassCostVo, ClassCostVo resultVo) {
        //x轴名字
        List<String> time=new ArrayList<>();
        //老师名字
        List<String> tname=new ArrayList<>();
        //课消次数
        List<List<Integer>> data=new ArrayList<>();
        //课消金额
        List<List<Float>> data2=new ArrayList<>();
        //存储多个老师在多个阶段的课消总次数
        Map<Long,List<Integer>> map1=new HashMap<>();
        //存储多个老师在多个阶段的课消总金额
        Map<Long,List<Float>> map2=new HashMap<>();
        int startFlag= start.getYear();
        int endFlag=end.getYear();
        for (int i = startFlag; i <= endFlag ; i++) {
            //x轴数据
            time.add(String.valueOf(i));
            end= start.plusYears(1);
            teacherClassCostVo = consumeService.getTeacherClassCostVo(start, end);
            if(i==startFlag){
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    List<Integer> temp1 = new ArrayList<>();
                    List<Float> temp2 = new ArrayList<>();
                    temp1.add(costVo.getCountChange());
                    temp2.add(costVo.getMoneyCost());
                    map1.put(costVo.getId(), temp1);
                    map2.put(costVo.getId(), temp2);
                }
            }else {
                for (TeacherClassCostVo costVo : teacherClassCostVo) {
                    map1.get(costVo.getId()).add(costVo.getCountChange());
                    map2.get(costVo.getId()).add(costVo.getMoneyCost());
                }
            }
            start=start.plusYears(1);
        }
        for (TeacherClassCostVo costVo : teacherClassCostVo) {
            tname.add(costVo.getName());
            data.add(map1.get(costVo.getId()));
            data2.add(map2.get(costVo.getId()));
        }
        resultVo.setTname(tname);
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }

    @Override
    public CardCostVo getClassCountByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("月课时统计");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("月");
        for(int i=1;i<=12;i++){
            endTime=startTime.plusMonths(1);
            time.add(i+"月");
            data.add(consumeService.getClassTimeCounts(startTime, endTime));
            startTime=startTime.plusMonths(1);
            if(startTime.isAfter(currentTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public CardCostVo getClassCountByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime endTime;
        LocalDateTime startTime;
        resultVo.setTitle("季度课时统计");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("季度");
        for(int i=1;i<=4;i++){
            endTime=startTime.plusMonths(3);
            time.add(i+"季度");
            data.add(consumeService.getClassTimeCounts(startTime,endTime));
            startTime=startTime.plusMonths(3);
            if(currentTime.isBefore(endTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public CardCostVo getClassCountByUnit3(StatisticsOfCardCostVo vo) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("年课时统计");
        startTime=LocalDateTime.of(vo.getBeginYear(),1,1,0,0);
        resultVo.setXname("年");
        for(int i = vo.getBeginYear(); i<= vo.getEndYear(); i++){
            endTime=startTime.plusYears(1);
            time.add(i+"年");
            data.add(consumeService.getClassTimeCounts(startTime,endTime));
            startTime=startTime.plusYears(1);
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        return resultVo;
    }

    @Override
    public CardCostVo getAddAndStreamCountByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        List<Integer> data2=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("月课时统计");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("月");
        for(int i=1;i<=12;i++){
            endTime=startTime.plusMonths(1);
            time.add(i+"月");
            LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(MemberEntity::getIsDeleted,0).between(MemberEntity::getCreateTime,startTime,endTime);
            data.add(memberService.count(qw));
            data2.add(memberService.getDeletedMemberCountsBetween(startTime,endTime));
            startTime=startTime.plusMonths(1);
            if(startTime.isAfter(currentTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }

    @Override
    public CardCostVo getAddAndStreamCountByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        List<Integer> data2=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime endTime;
        LocalDateTime startTime;
        resultVo.setTitle("季度课时统计");
        startTime=LocalDateTime.of(vo.getYearOfSelect(),1,1,0,0);
        resultVo.setXname("季度");
        for(int i=1;i<=4;i++){
            endTime=startTime.plusMonths(3);
            time.add(i+"季度");
            LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(MemberEntity::getIsDeleted,0).between(MemberEntity::getCreateTime,startTime,endTime);
            data.add(memberService.count(qw));
            data2.add(memberService.getDeletedMemberCountsBetween(startTime,endTime));
            startTime=startTime.plusMonths(3);
            if(currentTime.isBefore(endTime)) break;
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }

    @Override
    public CardCostVo getAddAndStreamCountByUnit3(StatisticsOfCardCostVo vo) {
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        List<Integer> data2=new ArrayList<>();
        CardCostVo resultVo=new CardCostVo();
        LocalDateTime startTime;
        LocalDateTime endTime;
        resultVo.setTitle("年课时统计");
        startTime=LocalDateTime.of(vo.getBeginYear(),1,1,0,0);
        resultVo.setXname("年");
        for(int i = vo.getBeginYear(); i<= vo.getEndYear(); i++){
            endTime=startTime.plusYears(1);
            time.add(i+"年");
            LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(MemberEntity::getIsDeleted,0).between(MemberEntity::getCreateTime,startTime,endTime);
            data.add(memberService.count(qw));
            data2.add(memberService.getDeletedMemberCountsBetween(startTime,endTime));
            startTime=startTime.plusYears(1);
        }
        resultVo.setTime(time);
        resultVo.setData(data);
        resultVo.setData2(data2);
        return resultVo;
    }


}

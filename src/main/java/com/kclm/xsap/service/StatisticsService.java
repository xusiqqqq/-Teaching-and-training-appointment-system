package com.kclm.xsap.service;

import com.kclm.xsap.vo.TeacherClassCostVo;
import com.kclm.xsap.vo.statistics.CardCostVo;
import com.kclm.xsap.vo.statistics.ClassCostVo;
import com.kclm.xsap.vo.statistics.StatisticsOfCardCostVo;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    int MONTH=0;
    int SEASON=1;
    int YEAR=2;
    CardCostVo getCardCostMonthOrSeasonOrYearByUnit3(StatisticsOfCardCostVo vo);
    CardCostVo getCardCostMonthOrSeasonOrYearByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime);
    CardCostVo getCardCostMonthOrSeasonOrYearByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime);

    //

    public ClassCostVo getClassCostVoByMonth(LocalDateTime startTime, LocalDateTime currentTime, List<TeacherClassCostVo> teacherClassCostVo,ClassCostVo resultVo);
    public ClassCostVo getClassCostVoBySeason( LocalDateTime startTime, LocalDateTime currentTime, List<TeacherClassCostVo> teacherClassCostVo,ClassCostVo resultVo);
    ClassCostVo getClassCostVoByYear(LocalDateTime start,LocalDateTime end,List<TeacherClassCostVo> teacherClassCostVo,ClassCostVo resultVo);

    CardCostVo getClassCountByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime);
    CardCostVo getClassCountByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime);
    CardCostVo getClassCountByUnit3(StatisticsOfCardCostVo vo);

    CardCostVo getAddAndStreamCountByUnit1(StatisticsOfCardCostVo vo, LocalDateTime currentTime);
    CardCostVo getAddAndStreamCountByUnit2(StatisticsOfCardCostVo vo, LocalDateTime currentTime);
    CardCostVo getAddAndStreamCountByUnit3(StatisticsOfCardCostVo vo);

}

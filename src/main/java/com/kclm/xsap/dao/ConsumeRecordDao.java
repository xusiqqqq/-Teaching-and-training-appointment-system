package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.vo.TeacherClassCostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConsumeRecordDao extends BaseMapper<ConsumeRecordEntity> {
    //获取某个会员某张卡消费的总金额；
    @Select("select sum(money_cost) from t_consume_record where member_bind_id=#{bindId}")
    BigDecimal getCountsMoneyByBindId(@Param("bindId") Long bindId);

    @Select("select sum(card_count_change) from t_consume_record where member_bind_id=#{bindId}")
    Integer getTimesByBindId(@Param("bindId") Long bindId);

    @Select("select e.id,e.name,sc.countChange,sc.moneyCost from t_employee e left join " +
            "(select  sum(cr.card_count_change) countChange,sum(cr.money_cost) moneyCost,sr.teacher_id from t_schedule_record sr " +
            "join t_consume_record cr on sr.id=cr.schedule_id where cr.create_time between #{start} and #{end} group by sr.teacher_id) as sc " +
            "on e.id=sc.teacher_id where e.role_type=0 and e.is_deleted=0")
    List<TeacherClassCostVo> getTeacherClassConsume(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);


    @Select("select sum(card_count_change) from t_consume_record where create_time BETWEEN #{start} AND #{end}")
    Integer getClassTimes(@Param("start")LocalDateTime start,@Param("end")LocalDateTime end);

}

package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.RechargeRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface RechargeRecordDao extends BaseMapper<RechargeRecordEntity> {

    @Select("select sum(r.received_money) from t_recharge_record r " +
            "where r.create_time BETWEEN #{start} and #{end}")
    BigDecimal getChargeCountByDay(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    @Select("select min(create_time) from t_recharge_record ")
    LocalDateTime getEarlyCreatTime();
}

package com.kclm.xsap.dao;

import com.kclm.xsap.entity.ReservationRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.ScheduleDetailReservedVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 预约记录
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface ReservationRecordDao extends BaseMapper<ReservationRecordEntity> {

    List<ReservationRecordEntity> selectNumberOfReservationByDay();

    List<ScheduleDetailReservedVo> selectReserveList(@Param("id") Long id, @Param("flag") String flag);

    List<ReservationRecordEntity> selectReserveInfo(@Param("id") Long id);
}

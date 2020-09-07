package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TReservationRecord;

public interface TReservationRecordMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TReservationRecord record);

    int insertSelective(TReservationRecord record);

    TReservationRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TReservationRecord record);

    int updateByPrimaryKey(TReservationRecord record);
}
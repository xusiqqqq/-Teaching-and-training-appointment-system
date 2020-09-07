package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TGlobalReservationSet;

public interface TGlobalReservationSetMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TGlobalReservationSet record);

    int insertSelective(TGlobalReservationSet record);

    TGlobalReservationSet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TGlobalReservationSet record);

    int updateByPrimaryKey(TGlobalReservationSet record);
}
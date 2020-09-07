package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TScheduleRecord;

public interface TScheduleRecordMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TScheduleRecord record);

    int insertSelective(TScheduleRecord record);

    TScheduleRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TScheduleRecord record);

    int updateByPrimaryKey(TScheduleRecord record);
}
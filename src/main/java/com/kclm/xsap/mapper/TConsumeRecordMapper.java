package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TConsumeRecord;

public interface TConsumeRecordMapper extends BaseMapper<TConsumeRecord> {

    int insert(TConsumeRecord record);

    int insertSelective(TConsumeRecord record);

    TConsumeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TConsumeRecord record);

    int updateByPrimaryKey(TConsumeRecord record);
}
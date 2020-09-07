package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TMemberBindRecord;

public interface TMemberBindRecordMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMemberBindRecord record);

    int insertSelective(TMemberBindRecord record);

    TMemberBindRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMemberBindRecord record);

    int updateByPrimaryKey(TMemberBindRecord record);
}
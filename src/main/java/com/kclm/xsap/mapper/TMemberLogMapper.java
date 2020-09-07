package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TMemberLog;

public interface TMemberLogMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMemberLog record);

    int insertSelective(TMemberLog record);

    TMemberLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMemberLog record);

    int updateByPrimaryKey(TMemberLog record);
}
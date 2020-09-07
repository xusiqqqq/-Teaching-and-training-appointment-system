package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TMember;

public interface TMemberMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMember record);

    int insertSelective(TMember record);

    TMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMember record);

    int updateByPrimaryKey(TMember record);
}
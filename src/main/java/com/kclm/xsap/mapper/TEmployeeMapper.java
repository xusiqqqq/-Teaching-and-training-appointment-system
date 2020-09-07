package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TEmployee;

public interface TEmployeeMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TEmployee record);

    int insertSelective(TEmployee record);

    TEmployee selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TEmployee record);

    int updateByPrimaryKey(TEmployee record);
}
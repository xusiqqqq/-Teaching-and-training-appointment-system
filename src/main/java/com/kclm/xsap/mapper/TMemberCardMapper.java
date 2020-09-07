package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TMemberCard;

public interface TMemberCardMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMemberCard record);

    int insertSelective(TMemberCard record);

    TMemberCard selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMemberCard record);

    int updateByPrimaryKey(TMemberCard record);
}
package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TCourseCard;
import org.apache.ibatis.annotations.Param;

public interface TCourseCardMapper extends BaseMapper {
    int deleteByPrimaryKey(@Param("cardId") Long cardId, @Param("cid") Long cid);

    int insert(TCourseCard record);

    int insertSelective(TCourseCard record);
}
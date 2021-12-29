package com.kclm.xsap.dao;

import com.kclm.xsap.entity.CourseCardEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 中间表：课程-会员卡
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@Mapper
public interface CourseCardDao extends BaseMapper<CourseCardEntity> {

    int saveCourseCard(@Param("cardId") Long cardId, @Param("courseId") Long courseId);
}

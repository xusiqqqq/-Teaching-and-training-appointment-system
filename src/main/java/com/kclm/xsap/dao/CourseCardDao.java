package com.kclm.xsap.dao;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.kclm.xsap.entity.CourseCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseCardDao extends MppBaseMapper<CourseCardEntity> {
    @Select("select c.name from t_member_card as c,t_course_card as cs " +
            "where c.id=cs.card_id and cs.course_id=#{courseId}")
    String[] getCardNameByCourseId(@Param("courseId") Long courseId);


    //通过课程id获取该课程所能支持的卡id
    @Select("select c.id from t_member_card as c,t_course_card as cs " +
            "where c.id=cs.card_id and cs.course_id=#{courseId}")
    List<Long> getCardIdByCourseId(@Param("courseId") Long courseId);


    @Select("select c.name " +
            "from t_course as c,t_course_card as cs " +
            "where c.id=cs.course_id and cs.card_id=#{cardId}")
    String[] getCourseByCardId(@Param("cardId") Long cardId);
}

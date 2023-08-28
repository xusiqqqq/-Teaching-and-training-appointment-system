package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseDao extends BaseMapper<CourseEntity> {
    @Select("select c.* " +
            "from t_course as c ,t_schedule_record as sr " +
            "where c.id=sr.course_id and sr.id=#{scheduleId};")
    CourseEntity getCourseEntityByScheduleId(@Param("scheduleId") Long scheduleId);
}

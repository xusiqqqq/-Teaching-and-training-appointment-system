package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.kclm.xsap.vo.ScheduleAddValidVo;
import com.kclm.xsap.vo.ScheduleForConsumeSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleRecordDao extends BaseMapper<ScheduleRecordEntity> {

    @Select("select sr.id scheduleId,c.name courseName,e.name teacherName,TIMESTAMP(sr.start_date,sr.class_time) as classDateTime " +
            "from t_schedule_record sr,t_course c,t_employee e " +
            "where c.id=sr.course_id and e.id=sr.teacher_id and TIMESTAMP(sr.start_date,sr.class_time) between #{earlyTwoWeeks} and #{current}")
    List<ScheduleForConsumeSearchVo> getScheduleForConsumeSearchVo(@Param("earlyTwoWeeks")LocalDateTime earlyTwoWeeks ,@Param("current")LocalDateTime currentTime);


//    @Select("select sr.id scheduleId,c.id courseId, c.duration,e.id teacherId,sr.class_time classTime" +
//            "from t_schedule_record sr,t_course c,t_employee e " +
//            "where c.id=sr.course_id and e.id=sr.teacher_id and e.id=#{teacherId} and TIMESTAMP(sr.start_date,sr.class_time) between #{earlyNinetyMinutes} and #{lateNinetyMinutes}")
    @Select("select sr.id scheduleId,c.id courseId, c.duration,e.id teacherId,sr.class_time classTime " +
            "from t_schedule_record sr,t_course c,t_employee e " +
            "where c.id=sr.course_id and e.id=sr.teacher_id and e.id=#{teacherId} and TIMESTAMP(sr.start_date,sr.class_time) between #{start} and #{end}")
    List<ScheduleAddValidVo> getScheduleAddValidVo(@Param("teacherId")Long teacherId,@Param("start")LocalDateTime start,@Param("end")LocalDateTime end);
}

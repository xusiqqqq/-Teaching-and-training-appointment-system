package com.kclm.xsap.dto.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TScheduleRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月18日 下午5:53:48 
 * @description 此类用来描述了排课计划DTO类型转换
 *
 */
@Mapper
public interface CourseScheduleConvert {

	CourseScheduleConvert INSTANCE = Mappers.getMapper(CourseScheduleConvert.class);
	
	/**
	 * 
	 * @param schedule	对应排课计划记录
	 * @param course	对应课程实体类
	 * @param supportCards	支持的会员卡种类
	 * @param teacherName	上课老师
	 * @param reserveDto	预约记录
	 * @param classDto	上课数据
	 * @return CourseScheduleDTO。要展示的排课计划信息
	 */
	@Mappings({
		@Mapping(source = "course.name",target = "courseName"),
		@Mapping(source = "course.contains",target = "classNumbers"),
		@Mapping(source = "course.limitSex",target = "limitSex"),
		@Mapping(source = "course.limitAge",target = "limitAge"),
		@Mapping(source = "schedule.id", target = "scheduleId"),
		@Mapping(source = "course.id", target = "courseId"),
		@Mapping(target = "startTime",expression = "java(LocalDateTime.of(schedule.getStartDate(),schedule.getClassTime() ))"),
		@Mapping(source = "reserveDto", target = "reserveRecord"),
		@Mapping(source = "classDto", target = "classRecord")
	})
	CourseScheduleDTO entity2Dto(TScheduleRecord schedule,TCourse course,
			String supportCards,String teacherName,List<ReserveRecordDTO> reserveDto, List<ClassRecordDTO> classDto); 

}
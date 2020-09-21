package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TEmployee;
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
	 * @param emp	对应员工实体类
	 * @param reserveDTO	对应预约记录
	 * @return	CourseScheduleDTO。要展示的排课计划信息
	 */
	@Mapping(source = "course.name",target = "courseName")
	@Mapping(source = "course.contains",target = "classNumbers")
	@Mapping(source = "emp.name",target = "teacher")
	@Mapping(target = "startTime",expression = "java (LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime()))")
	CourseScheduleDTO entity2Dto(TScheduleRecord schedule,TCourse course,TEmployee emp,ReserveRecordDTO reserveDTO);
	
}

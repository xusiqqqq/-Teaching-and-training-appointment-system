package com.kclm.xsap.dto.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

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
	 * 未使用
	 * @return	CourseScheduleDTO。要展示的排课计划信息
	 */
//	@Mappings({
//		@Mapping(source = "course.name",target = "courseName"),
//		@Mapping(target = "startTime",expression = "java (LocalDateTime.of(schedule.getStartDate(),schedule.getClassTime() ))")		
//
//	})
//	default CourseScheduleDTO entity2Dto(TScheduleRecord schedule,TCourse course) {
//		
//	}
	
}

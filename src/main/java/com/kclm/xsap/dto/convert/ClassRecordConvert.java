package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月18日 下午3:31:02 
 * @description 此类用来描述了上课记录DTO类型转换
 *
 */
@Mapper
public interface ClassRecordConvert {

	ClassRecordConvert INSTANCE = Mappers.getMapper(ClassRecordConvert.class);
	
	/**
	 * 
	 * @param classRecord 对应预约记录实体类。status = 1，表示上课记录
	 * @param course	对应课程表实体类
	 * @param schedule	对应排课计划实体类
	 * @param card	对应会员卡实体类
	 * @return ClassRecordDTO。上课记录要展示的信息
	 */
	@Mappings({
		@Mapping(source = "card.name",target = "cardName"),
		@Mapping(source = "course.name",target = "courseName"),
		@Mapping(source = "schedule.orderNums",target = "reserveNumbers"),
		@Mapping(source = "classRecord.note",target = "reserveNote"),
		@Mapping(source = "classRecord.id",target = "classRecordId"),
		@Mapping(source = "course.id",target = "courseId"),
		@Mapping(source = "schedule.id",target = "scheduleId"),
		@Mapping(source = "card.id",target = "cardId"),
		@Mapping(target = "classTime",expression = "java(LocalDateTime.of(schedule.getStartDate(),schedule.getClassTime() ))")
	})
	ClassRecordDTO entity2Dto(TReservationRecord classRecord,TCourse course,TScheduleRecord schedule,TMemberCard card);
	
	
}

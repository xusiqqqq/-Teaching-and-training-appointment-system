package com.kclm.xsap.dto.convert;

import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.CourseEntity;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月21日 上午9:53:10 
 * @description 此类用来描述了预约记录的信息DTO转换
 *
 */
@Mapper(componentModel = "spring")
public interface ReserveRecordConvert {

	//ReserveRecordConvert INSTANCE = Mappers.getMapper(ReserveRecordConvert.class);
	
	/**
	 * 排课计划表所查阅的预约记录，关系到多个会员
	 * @param course	对应课程实体类
	 * @param schedule	对应排课计划记录
	 * @param reserve	对应预约记录
	 * @param member	对应会员实体类
	 * @return	ReserveRecordDTO。显示预约记录的信息
	 */
	@Mappings({	
		@Mapping(source = "course.name",target = "courseName"),
		@Mapping(source = "member.name",target = "memberName"),
		@Mapping(source = "schedule.orderNums",target = "reserveNumbers"),
		@Mapping(source = "reserve.createTime",target = "reserveTime"),
		@Mapping(source = "reserve.lastModifyTime",target = "operateTime"),
		@Mapping(source = "reserve.note",target = "reserveNote"),
		@Mapping(source = "reserve.status",target = "reserveStatus"),
		@Mapping(source = "course.id", target = "courseId"),
		@Mapping(source = "schedule.id", target = "reserveId"),
		@Mapping(source = "member.id", target = "scheduleId"),
		@Mapping(source = "reserve.id", target = "memberId")
	})

	ReserveRecordDTO entity2Dto(CourseEntity course, ScheduleRecordEntity schedule, ReservationRecordEntity reserve, MemberEntity member);

}

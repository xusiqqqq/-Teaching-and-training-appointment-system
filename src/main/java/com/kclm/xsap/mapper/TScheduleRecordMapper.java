package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.entity.TScheduleRecord;

@Mapper
public interface TScheduleRecordMapper extends BaseMapper<TScheduleRecord> {

	/**
	 * 	基本排课信息 - 有会员卡支持
	 * @param scheduleId
	 * @return
	 */
	CourseScheduleDTO oneScheduleView(Long scheduleId);
	
	/**
	 * 	基本排课信息 - 没有会员卡支持
	 * @param scheduleId
	 * @return
	 */
	CourseScheduleDTO oneScheduleNoCardView(Long scheduleId);
	
	/**
	 * 	上课数据
	 * @param scheduleId
	 * @return
	 */
	List<ClassRecordDTO> listClassView(Long scheduleId);
}
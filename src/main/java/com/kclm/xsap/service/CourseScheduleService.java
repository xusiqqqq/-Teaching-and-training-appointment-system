package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import com.kclm.xsap.entity.TScheduleRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午3:44:42 
 * @description 此类用来描述了团课排课管理业务
 *
 */
public interface CourseScheduleService {

	boolean save(TScheduleRecord schedule);
	
	boolean deleteById(Integer id);

	/**
	 * 获取所有的排课记录
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return List<TScheduleRecord>。团课排课记录结果集
	 */
	List<TScheduleRecord> listSchedule(LocalDate startDate,LocalDate endDate);
	
	/**
	 * 根据id查询到匹配的排课记录
	 * @param scheduleId 排课记录id
	 * @return TScheduleRecord。排课记录
	 */
	TScheduleRecord findById(Integer scheduleId);
	
}

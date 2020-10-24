package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import com.kclm.xsap.dto.CourseScheduleDTO;
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
	
	/**
	 * 	复制排课
	 * @param sourseDate	原排课日程
	 * @param targetDate	目标排课日程
	 * @return
	 */
	boolean copySchedule(LocalDate sourceDate, LocalDate targetDate);
	
	boolean deleteById(Long id);

	/**
<<<<<<< HEAD
	 * 	获取所有的排课记录
=======
	 * 	所有的排课记录
>>>>>>> b3286e860875dba3e734146520c65f0598ff525f
	 * @return
	 */
	List<CourseScheduleDTO> listScheduleAll();
	
	/**
	 * 获取给定的日期范围内所有的排课记录
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return List<CourseScheduleDTO>。团课排课记录结果集
	 */
	List<CourseScheduleDTO> listSchedule(LocalDate startDate,LocalDate endDate);
	
	/**
	 * 根据id查询到匹配的排课记录
	 * @param scheduleId 排课记录id
	 * @return CourseScheduleDTO。排课记录
	 */
	CourseScheduleDTO findById(Long scheduleId);
	
}
